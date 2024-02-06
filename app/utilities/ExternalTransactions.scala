package utilities

import com.assetmantle.modules.assets.{transactions => assetTransactions}
import com.assetmantle.modules.identities.{transactions => identityTransactions}
import com.assetmantle.modules.orders.{transactions => orderTransactions}
import com.cosmos.bank.{v1beta1 => bankTx}
import com.ibc.applications.transfer.v2.FungibleTokenPacketData
import com.ibc.core.channel.{v1 => channelTx}
import models.analytics.CollectionsAnalysis
import models.blockchain.{Order, Split}
import models.common.Coin
import models.master._
import models.{blockchain, master, masterTransaction}
import play.api.Configuration
import schema.data.base.{HeightData, NumberData}
import schema.id.base._
import schema.list.PropertyList
import schema.property.base.{MesaProperty, MetaProperty}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala

@Singleton
class ExternalTransactions @Inject()(
                                      blockchainBlocks: blockchain.Blocks,
                                      blockchainOrders: blockchain.Orders,
                                      blockchainSplits: blockchain.Splits,
                                      blockchainParameters: blockchain.Parameters,
                                      collectionsAnalysis: CollectionsAnalysis,
                                      masterAccounts: master.Accounts,
                                      masterBurntNFTs: master.BurntNFTs,
                                      masterCollections: master.Collections,
                                      masterKeys: master.Keys,
                                      masterNFTs: master.NFTs,
                                      masterNFTProperties: master.NFTProperties,
                                      masterNFTOwners: master.NFTOwners,
                                      masterNFTTags: master.NFTTags,
                                      masterWishlists: master.WishLists,
                                      masterSecondaryMarkets: master.SecondaryMarkets,
                                      masterTransactionExternalAssets: masterTransaction.ExternalAssets,
                                      notification: Notification,
                                    )
                                    (implicit executionContext: ExecutionContext, configuration: Configuration) {

  private def updateNFTOwner(nft: Option[NFT], account: Option[Account]) = if (nft.isDefined && account.isDefined) {
    val split = blockchainSplits.Service.tryGetByOwnerIDAndAssetID(ownerId = account.get.getIdentityID, assetID = nft.get.getAssetID)
    val nftOwner = masterNFTOwners.Service.get(nftId = nft.get.id, ownerId = account.get.id)
    val collection = masterCollections.Service.tryGet(nft.get.collectionId)

    def checkAndUpdate(split: Split, nftOwner: Option[NFTOwner], collection: Collection) = if (nftOwner.isDefined) {
      masterNFTOwners.Service.update(nftOwner.get.copy(quantity = split.value.toLong))
    } else masterNFTOwners.Service.add(NFTOwner(nftId = nft.get.id, ownerId = account.get.id, creatorId = collection.creatorId, collectionId = collection.id, quantity = split.value.toLong, saleId = None, publicListingId = None))

    for {
      split <- split
      nftOwner <- nftOwner
      collection <- collection
      _ <- checkAndUpdate(split, nftOwner, collection)
    } yield ()
  } else Future()

  def onSendCoin(sendCoin: bankTx.MsgSend): Future[Unit] = {
    val keys = masterKeys.Service.getKeysByAddresses(Seq(sendCoin.getFromAddress, sendCoin.getToAddress))

    def sendNotifications(keys: Seq[Key]): Unit = if (keys.nonEmpty) {
      keys.filter(_.address == sendCoin.getFromAddress).map(_.accountId).foreach(x => notification.send(x, constants.Notification.SEND_COIN_FROM_ACCOUNT, sendCoin.getAmountList.asScala.map(x => Coin(x).getAmountWithNormalizedDenom()).mkString(", "), sendCoin.getToAddress)(""))
      keys.filter(_.address == sendCoin.getToAddress).map(_.accountId).foreach(x => notification.send(x, constants.Notification.SEND_COIN_TO_ACCOUNT, sendCoin.getAmountList.asScala.map(x => Coin(x).getAmountWithNormalizedDenom()).mkString(", "), sendCoin.getFromAddress)(""))
    }

    for {
      keys <- keys
    } yield sendNotifications(keys)
  }

  def onIBCReceive(receivePacket: channelTx.MsgRecvPacket): Future[Unit] = {
    val packet = FungibleTokenPacketData.parseFrom(receivePacket.getPacket.getData)
    val keys = if (packet.getReceiver.startsWith(constants.Blockchain.AccountPrefix)) masterKeys.Service.getKeysByAddresses(Seq(packet.getReceiver)) else Future(Seq())

    def sendNotifications(keys: Seq[Key]): Unit = if (keys.nonEmpty) {
      keys.map(_.accountId).foreach(x => notification.send(x, constants.Notification.IBC_RECEIVED_TOKEN, Coin(denom = packet.getDenom, amount = MicroNumber(packet.getAmount.toLong / MicroNumber.factor)).getAmountWithNormalizedDenom(), packet.getReceiver)(""))
    }

    for {
      keys <- keys
    } yield sendNotifications(keys)
  }

  def onBurnNFT(msg: assetTransactions.burn.Message, txHash: String): Future[Unit] = {
    val nft = masterNFTs.Service.getByAssetId(AssetID(msg.getAssetID))

    def collection(collectionId: Option[String]) = collectionId.fold[Future[Option[Collection]]](Future(None))(x => masterCollections.Service.tryGet(x).map(x => Option(x)))

    def nftProperties(nftId: Option[String]) = nftId.fold[Future[Seq[NFTProperty]]](Future(Seq()))(x => masterNFTProperties.Service.getForNFT(x))

    def delete(nft: Option[NFT], collection: Option[Collection], nftProperties: Seq[NFTProperty]): Future[Unit] = if (nft.isDefined && collection.isDefined) {
      val addToBurnt = masterBurntNFTs.Service.add(nftId = nft.get.id, txHash = txHash, collectionId = nft.get.collectionId, assetId = AssetID(msg.getAssetID).asString, classificationId = collection.get.classificationId.getOrElse(""), supply = nft.get.totalSupply, name = nft.get.name, description = nft.get.description, properties = nftProperties.map(_.toBaseNFTProperty), fileExtension = nft.get.fileExtension)
      val deleteWishlists = masterWishlists.Service.deleteForNFT(nft.get.id)
      val deleteTags = masterNFTTags.Service.deleteByNFTId(nft.get.id)
      val deleteProperties = masterNFTProperties.Service.deleteByNFTId(nft.get.id)
      val deleteOwners = masterNFTOwners.Service.deleteByNFT(nft.get.id)
      val updateAnalysis = collectionsAnalysis.Utility.onBurn(collection.get.id)

      def deleteNFT() = masterNFTs.Service.delete(nft.get.id)

      for {
        _ <- addToBurnt
        _ <- deleteWishlists
        _ <- deleteTags
        _ <- deleteProperties
        _ <- deleteOwners
        _ <- updateAnalysis
        _ <- deleteNFT()
      } yield ()
    } else Future()

    for {
      nft <- nft
      collection <- collection(nft.map(_.id))
      nftProperties <- nftProperties(nft.map(_.id))
      _ <- delete(nft, collection, nftProperties)
    } yield ()
  }

  def onMutateNFT(msg: assetTransactions.mutate.Message): Future[Unit] = {
    val nft = masterNFTs.Service.getByAssetId(AssetID(msg.getAssetID))

    def updateProperties(nft: Option[NFT]) = if (nft.isDefined) {
      val metaProperties = PropertyList(msg.getMutableMetaProperties).getProperties.map(x => {
        val metaProperty = x.asInstanceOf[MetaProperty]
        utilities.Properties.metaPropertyToNFTProperty(nftId = nft.get.id, metaProperty = metaProperty, mutable = true)
      })
      val mesaProperties = PropertyList(msg.getMutableProperties).getProperties.map(x => {
        val mesaProperty = x.asInstanceOf[MesaProperty]
        utilities.Properties.mesaPropertyToNFTProperty(nftId = nft.get.id, mesaProperty = mesaProperty, mutable = true)
      })
      masterNFTProperties.Service.updateMultiple(metaProperties ++ mesaProperties)
    } else Future()

    for {
      nft <- nft
      _ <- updateProperties(nft)
    } yield ()
  }

  def onRenumerateNFT(msg: assetTransactions.renumerate.Message): Future[Unit] = {
    val nft = masterNFTs.Service.getByAssetId(AssetID(msg.getAssetID))
    val account = masterAccounts.Service.getByIdentityId(IdentityID(msg.getFromID))
    val split = blockchainSplits.Service.tryGetByOwnerIDAndAssetID(ownerId = IdentityID(msg.getFromID), assetID = AssetID(msg.getAssetID))

    def updateSupply(nft: Option[NFT]) = if (nft.isDefined) {
      val totalSupply = blockchainSplits.Service.getTotalSupply(AssetID(msg.getAssetID))

      def nftUpdate(totalSupply: BigInt) = masterNFTs.Service.update(nft.get.copy(totalSupply = totalSupply.toLong))

      for {
        totalSupply <- totalSupply
        _ <- nftUpdate(totalSupply)
      } yield ()
    } else Future()

    def updateOwner(nft: Option[NFT], account: Option[Account], split: Split) = if (nft.isDefined && account.isDefined) {
      val nftOwner = masterNFTOwners.Service.tryGet(nftId = nft.get.id, ownerId = account.get.id).map(Option(_))

      def update(nftOwner: Option[NFTOwner]) = if (nftOwner.isDefined) masterNFTOwners.Service.update(nftOwner.get.copy(quantity = split.value.toLong))
      else Future(0)

      for {
        nftOwner <- nftOwner
        _ <- update(nftOwner)
      } yield ()
    } else Future()

    for {
      nft <- nft
      _ <- updateSupply(nft)
      account <- account
      split <- split
      _ <- updateOwner(nft, account, split)
    } yield ()
  }

  def onProvisionIdentity(msg: identityTransactions.provision.Message): Future[Unit] = {
    val account = masterAccounts.Service.getByIdentityId(IdentityID(msg.getIdentityID))

    def update(account: Option[Account]) = if (account.isDefined) {
      val totalKeys = masterKeys.Service.countKeys(account.get.id)

      def add(totalKeys: Int) = masterKeys.Service.addUnmanagedKey(
        accountId = account.get.id,
        address = msg.getTo,
        password = "",
        name = "NEW_KEY_" + (totalKeys + 1).toString,
        retryCounter = 0,
        backupUsed = true,
        active = false,
        verified = Option(true)
      )

      for {
        totalKeys <- totalKeys
        _ <- add(totalKeys)
      } yield ()
    } else Future()

    for {
      account <- account
      _ <- update(account)
    } yield ()
  }

  def onUnprovisionIdentity(msg: identityTransactions.unprovision.Message): Future[Unit] = {
    val account = masterAccounts.Service.getByIdentityId(IdentityID(msg.getIdentityID))

    def update(account: Option[Account]) = if (account.isDefined) {
      masterKeys.Service.delete(accountId = account.get.id, address = msg.getTo)
    } else Future(0)

    for {
      account <- account
      _ <- update(account)
    } yield ()
  }

  def onOrderCancel(msg: orderTransactions.cancel.Message): Future[Unit] = {
    val secondaryMarket = masterSecondaryMarkets.Service.getByOrderId(OrderID(msg.getOrderID))

    def update(secondaryMarket: Option[SecondaryMarket]) = if (secondaryMarket.isDefined) {
      val updateMarket = masterSecondaryMarkets.Service.markOnCancellation(secondaryMarket.get.id)

      def updateAnalytics() = collectionsAnalysis.Utility.onCancelSecondaryMarket(secondaryMarket.get.collectionId, 1)

      for {
        _ <- updateMarket
        _ <- updateAnalytics()
      } yield Future(1)

    } else Future(0)

    for {
      secondaryMarket <- secondaryMarket
      _ <- update(secondaryMarket)
    } yield ()
  }

  def onPutOrder(msg: orderTransactions.put.Message, txHeight: Long): Future[Unit] = {
    val nft = masterNFTs.Service.getByAssetId(AssetID(msg.getMakerAssetID).asString)
    val account = masterAccounts.Service.getByIdentityId(IdentityID(msg.getFromID))
    val takerAssetID = AssetID(msg.getTakerAssetID)

    val orderID = utilities.Order.getOrderID(
      makerID = IdentityID(msg.getFromID),
      makerAssetID = AssetID(msg.getMakerAssetID),
      makerSplit = NumberData(BigInt(msg.getMakerSplit)),
      expiryHeight = HeightData(msg.getExpiryHeight.getValue),
      takerAssetID = takerAssetID,
      takerSplit = NumberData(BigInt(msg.getTakerSplit))
    )
    val blockchainOrder = blockchainOrders.Service.get(orderID.getBytes)

    def nftOwner(nft: Option[NFT], account: Option[Account]) = if (nft.isDefined && account.isDefined) {
      masterNFTOwners.Service.tryGet(nftId = nft.get.id, ownerId = account.get.id).map(Option(_))
    } else Future(None)

    def update(nft: Option[NFT], account: Option[Account], nftOwner: Option[NFTOwner], blockchainOrder: Option[Order]) = if (nft.isDefined && account.isDefined && nftOwner.isDefined && AssetID(msg.getTakerAssetID).asString == constants.Blockchain.StakingTokenAssetID.asString && blockchainOrder.isDefined && constants.SecondaryMarket.AssetTokensAllowed.map(_.asString).contains(takerAssetID.asString)) {
      val secondaryMarketId = utilities.IdGenerator.getRandomHexadecimal
      val endsIn = (((msg.getExpiryHeight.getValue - txHeight) * constants.Blockchain.MaxOrderHours) / constants.Blockchain.MaxOrderExpiry) * 3600L
      val secondaryMarket = SecondaryMarket(id = secondaryMarketId, orderId = orderID.asString, nftId = nft.get.id, collectionId = nft.get.collectionId, sellerId = account.get.id, quantity = BigInt(msg.getMakerSplit), price = MicroNumber(BigDecimal(msg.getTakerSplit).toBigInt), denom = constants.Blockchain.StakingToken, endsIn = endsIn, externallyMade = true, completed = false, cancelled = false, expired = false, status = Option(true))
      val add = masterSecondaryMarkets.Service.add(secondaryMarket)
      val collection = masterCollections.Service.markListedOnSecondaryMarket(nft.get.collectionId)
      val updateAnalytics = collectionsAnalysis.Utility.onCreateSecondaryMarket(nft.get.collectionId, totalListed = 1, listingPrice = secondaryMarket.price)

      def updateOwner() = masterNFTOwners.Service.onSecondaryMarket(nftId = nftOwner.get.nftId, ownerId = secondaryMarket.sellerId, sellQuantity = BigInt(msg.getMakerSplit))

      for {
        _ <- add
        _ <- collection
        _ <- updateOwner()
        _ <- updateAnalytics
      } yield ()
    } else Future(0)

    for {
      nft <- nft
      account <- account
      blockchainOrder <- blockchainOrder
      nftOwner <- nftOwner(nft, account)
      _ <- update(nft, account, nftOwner, blockchainOrder)
    } yield ()
  }

  def onGetOrder(msg: orderTransactions.get.Message): Future[Unit] = {
    val blockchainOrder = blockchainOrders.Service.tryGet(OrderID(msg.getOrderID))
    val account = masterAccounts.Service.getByIdentityId(IdentityID(msg.getFromID))
    val secondaryMarket = masterSecondaryMarkets.Service.getByOrderId(OrderID(msg.getOrderID))

    def nft(assetID: AssetID, account: Option[Account]) = if (account.isDefined) masterNFTs.Service.getByAssetId(assetID.asString) else Future(None)

    def updateAnalytics(secondaryMarket: Option[SecondaryMarket]) = if (secondaryMarket.isDefined) collectionsAnalysis.Utility.onSuccessfulBuyFromMarket(secondaryMarket.get.collectionId, secondaryMarket.get.price, 1) else Future()

    for {
      blockchainOrder <- blockchainOrder
      account <- account
      nft <- nft(blockchainOrder.getMakerAssetID, account)
      _ <- updateNFTOwner(nft, account)
      secondaryMarket <- secondaryMarket
      _ <- updateAnalytics(secondaryMarket)
    } yield ()
  }

  def onAssetSend(msg: assetTransactions.send.Message): Future[Unit] = {
    val nft = masterNFTs.Service.getByAssetId(AssetID(msg.getAssetID).asString)
    val fromAccount = masterAccounts.Service.getByIdentityId(IdentityID(msg.getFromID))
    val toAccount = masterAccounts.Service.getByIdentityId(IdentityID(msg.getToID))

    for {
      nft <- nft
      fromAccount <- fromAccount
      toAccount <- toAccount
      _ <- updateNFTOwner(nft, fromAccount)
      _ <- updateNFTOwner(nft, toAccount)
    } yield ()
  }
  //
  //  def onSplitUnwrap(msg: splitTransactions.unwrap.Message): Future[Unit] = if (OwnableID(msg.getOwnableID).asString != constants.Blockchain.StakingTokenCoinID.asString) {
  //    val nft = masterNFTs.Service.getByAssetId(OwnableID(msg.getOwnableID).asString)
  //    val fromAccount = masterAccounts.Service.getByIdentityId(IdentityID(msg.getFromID))
  //
  //    def update(nft: Option[NFT], account: Option[Account]) = if (nft.isDefined && account.isDefined) {
  //      val split = blockchainSplits.Service.getByOwnerIDAndOwnableID(ownerId = account.get.getIdentityID, ownableID = nft.get.getAssetID)
  //      val nftOwner = masterNFTOwners.Service.get(nftId = nft.get.id, ownerId = account.get.id)
  //      val collection = masterCollections.Service.tryGet(nft.get.collectionId)
  //
  //      def checkAndUpdate(split: Option[Split], nftOwner: Option[NFTOwner], collection: Collection) = if (nftOwner.isDefined) {
  //        masterNFTOwners.Service.update(nftOwner.get.copy(quantity = (split.get.value * AttoNumber.factor).toLong))
  //      } else masterNFTOwners.Service.add(NFTOwner(nftId = nft.get.id, ownerId = account.get.id, creatorId = collection.creatorId, collectionId = collection.id, quantity = (split.value * AttoNumber.factor).toLong, saleId = None, publicListingId = None, secondaryMarketId = None))
  //
  //      for {
  //        split <- split
  //        nftOwner <- nftOwner
  //        collection <- collection
  //        _ <- checkAndUpdate(split, nftOwner, collection)
  //      } yield ()
  //    } else Future()
  //
  //    for {
  //      nft <- nft
  //      fromAccount <- fromAccount
  //      _ <- update(nft, fromAccount)
  //    } yield ()
  //  } else Future()
}
