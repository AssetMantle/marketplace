package utilities

import com.assetmantle.modules.assets.{transactions => assetTransactions}
import com.assetmantle.modules.identities.{transactions => identityTransactions}
import com.assetmantle.modules.orders.{transactions => orderTransactions}
import com.assetmantle.modules.splits.{transactions => splitTransactions}
import com.cosmos.bank.{v1beta1 => bankTx}
import com.ibc.applications.transfer.v2.FungibleTokenPacketData
import com.ibc.core.channel.{v1 => channelTx}
import models.analytics.CollectionsAnalysis
import models.blockchain.Split
import models.common.Coin
import models.master._
import models.{blockchain, master, masterTransaction}
import play.api.Configuration
import schema.id.OwnableID
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

  def onSendCoin(sendCoin: bankTx.MsgSend): Future[Unit] = {
    val keys = masterKeys.Service.getKeysByAddresses(Seq(sendCoin.getFromAddress, sendCoin.getToAddress))

    def sendNotifications(keys: Seq[Key]): Unit = if (keys.nonEmpty) {
      //      keys.filter(_.address == sendCoin.getFromAddress).map(_.accountId).foreach(x => notification.send(x, constants.Notification.SEND_COIN_FROM_ACCOUNT, sendCoin.getAmountList.asScala.map(x => Coin(x).getAmountWithNormalizedDenom()).mkString(", "), sendCoin.getFromAddress)(""))
      keys.filter(_.address == sendCoin.getToAddress).map(_.accountId).foreach(x => notification.send(x, constants.Notification.SEND_COIN_TO_ACCOUNT, sendCoin.getAmountList.asScala.map(x => Coin(x).getAmountWithNormalizedDenom()).mkString(", "), sendCoin.getToAddress)(""))
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
        utilities.NFT.metaPropertyToNFTProperty(nftId = nft.get.id, metaProperty = metaProperty, mutable = true)
      })
      val mesaProperties = PropertyList(msg.getMutableProperties).getProperties.map(x => {
        val mesaProperty = x.asInstanceOf[MesaProperty]
        utilities.NFT.mesaPropertyToNFTProperty(nftId = nft.get.id, mesaProperty = mesaProperty, mutable = true)
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
    val split = blockchainSplits.Service.tryGetByOwnerIDAndOwnableID(ownerId = IdentityID(msg.getFromID), ownableID = AssetID(msg.getAssetID))

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
      masterSecondaryMarkets.Service.markOnCancellation(secondaryMarket.get.id)
    } else Future(0)

    for {
      secondaryMarket <- secondaryMarket
      _ <- update(secondaryMarket)
    } yield ()
  }

  def onOrderMake(msg: orderTransactions.make.Message, txHeight: Long): Future[Unit] = {
    val orderClassificationId = ClassificationID(msg.getClassificationID)
    val nft = masterNFTs.Service.getByAssetId(OwnableID(msg.getMakerOwnableID).asString)
    val account = masterAccounts.Service.getByIdentityId(IdentityID(msg.getFromID))

    def nftOwner(nft: Option[NFT], account: Option[Account]) = if (nft.isDefined && account.isDefined) {
      masterNFTOwners.Service.tryGet(nftId = nft.get.id, ownerId = account.get.id).map(Option(_))
    } else Future(None)

    def update(nft: Option[NFT], account: Option[Account], nftOwner: Option[NFTOwner]) = if (nft.isDefined && account.isDefined && nftOwner.isDefined && OwnableID(msg.getTakerOwnableID).asString == constants.Blockchain.StakingTokenCoinID.asString) {
      if (orderClassificationId.asString == constants.Transaction.OrderClassificationID.asString) {
        val orderID = utilities.Order.getOrderID(
          classificationID = constants.Transaction.OrderClassificationID,
          properties = constants.Orders.ImmutableMetas,
          makerID = IdentityID(msg.getFromID),
          makerOwnableID = nft.get.getAssetID,
          makerOwnableSplit = BigInt(msg.getMakerOwnableSplit),
          creationHeight = txHeight,
          takerID = IdentityID(msg.getTakerID),
          takerOwnableID = constants.Blockchain.StakingTokenCoinID,
          takerOwnableSplit = BigInt(msg.getTakerOwnableSplit)
        )
        val secondaryMarketId = utilities.IdGenerator.getRandomHexadecimal
        val endHours = (txHeight + msg.getExpiresIn.getValue - blockchainBlocks.Service.getLatestHeight) * 6
        val secondaryMarket = SecondaryMarket(id = secondaryMarketId, orderId = Option(orderID.asString), nftId = nft.get.id, collectionId = nft.get.collectionId, sellerId = account.get.id, quantity = msg.getMakerOwnableSplit.toInt, price = MicroNumber(BigDecimal(msg.getTakerOwnableSplit).toBigInt + 1), denom = constants.Blockchain.StakingToken, endHours = endHours.toInt, externallyMade = true, completed = false, cancelled = false, expired = txHeight + msg.getExpiresIn.getValue >= blockchainBlocks.Service.getLatestHeight, failed = true)
        val add = masterSecondaryMarkets.Service.add(secondaryMarket)

        def updateOwner() = masterNFTOwners.Service.onSecondaryMarket(nftOwner.get.nftId, secondaryMarketId, sellQuantity = BigInt(msg.getMakerOwnableSplit))

        for {
          _ <- add
          _ <- updateOwner()
        } yield ()
      } else {
        val quantity = BigInt(msg.getMakerOwnableSplit)
        val updateOrDeleteNFTOwner = if (quantity == nftOwner.get.quantity) masterNFTOwners.Service.delete(nftId = nftOwner.get.nftId, ownerId = nftOwner.get.ownerId)
        else if (quantity < nftOwner.get.quantity) masterNFTOwners.Service.update(nftOwner.get.copy(quantity = nftOwner.get.quantity - quantity))
        else Future()

        def transfer = masterTransactionExternalAssets.Service.insertOrUpdate(nftId = nft.get.id, lastOwnerId = nftOwner.get.ownerId, assetId = nft.get.assetId.get, currentOwnerIdentityId = schema.constants.ID.OrderIdentityID.asString, collectionId = nft.get.collectionId, amount = quantity)

        for {
          _ <- updateOrDeleteNFTOwner
          _ <- transfer
        } yield ()
      }
    } else Future(0)

    for {
      nft <- nft
      account <- account
      nftOwner <- nftOwner(nft, account)
      _ <- update(nft, account, nftOwner)
    } yield ()
  }

  def onOrderTake(msg: orderTransactions.take.Message): Future[Unit] = {
    val blockchainOrder = blockchainOrders.Service.tryGet(OrderID(msg.getOrderID))
    val account = masterAccounts.Service.getByIdentityId(IdentityID(msg.getFromID))

    def nft(ownableID: OwnableID, account: Option[Account]) = if (account.isDefined) masterNFTs.Service.getByAssetId(ownableID.asString) else Future(None)

    def update(nft: Option[NFT], account: Option[Account]) = if (nft.isDefined && account.isDefined) {
      val split = blockchainSplits.Service.tryGetByOwnerIDAndOwnableID(ownerId = account.get.getIdentityID, ownableID = nft.get.getAssetID)
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

    for {
      blockchainOrder <- blockchainOrder
      account <- account
      nft <- nft(blockchainOrder.getMakerOwnableID, account)
      _ <- update(nft, account)
    } yield ()
  }

  def onSplitSend(msg: splitTransactions.send.Message): Future[Unit] = {
    val nft = masterNFTs.Service.getByAssetId(OwnableID(msg.getOwnableID).asString)
    val fromAccount = masterAccounts.Service.getByIdentityId(IdentityID(msg.getFromID))
    val toAccount = masterAccounts.Service.getByIdentityId(IdentityID(msg.getToID))

    def update(nft: Option[NFT], account: Option[Account]) = if (nft.isDefined && account.isDefined) {
      val split = blockchainSplits.Service.tryGetByOwnerIDAndOwnableID(ownerId = account.get.getIdentityID, ownableID = nft.get.getAssetID)
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

    for {
      nft <- nft
      fromAccount <- fromAccount
      toAccount <- toAccount
      _ <- update(nft, fromAccount)
      _ <- update(nft, toAccount)
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
