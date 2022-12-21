package service

import models.common.Collection.SocialProfile
import models.master
import models.master.{Collection, CollectionFile}
import play.api.libs.json.Reads
import play.api.{Configuration, Logger}

import java.io.File
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.io.{Source => ScalaSource}

@Singleton
class Starter @Inject()(
                         masterAccounts: master.Accounts,
                         masterCollections: master.Collections,
                         masterCollectionFiles: master.CollectionFiles,
                         masterNFTs: master.NFTs,
                         masterNFTProperties: master.NFTProperties,
                         masterWishLists: master.WishLists,
                         utilitiesOperations: utilities.Operations,
                       )(implicit exec: ExecutionContext, configuration: Configuration) {

  private implicit val module: String = constants.Module.STARTER_SERVICE

  private implicit val logger: Logger = Logger(this.getClass)

  private val uploadCollectionFilePath = constants.CommonConfig.Files.CollectionPath + "/upload.json"

  def getListOfFiles(dir: String): List[String] = {
    val file = new File(dir)
    file.listFiles.filter(_.isFile)
      .map(_.getPath).toList
  }

  def readFile[T](path: String)(implicit reads: Reads[T]): Future[T] = Future {
    val source = ScalaSource.fromFile(path)
    val obj = utilities.JSON.convertJsonStringToObject[T](source.mkString)
    source.close()
    obj
  }

  private def updateCollectionAwsFiles() = {
    val collectionFiles = masterCollectionFiles.Service.fetchAll()
    val collections = masterCollections.Service.fetchAll()

    def update(collectionFiles: Seq[CollectionFile], collections: Seq[Collection]): Unit = collectionFiles.foreach { collectionFile =>
      try {
        val sourceKey = collections.find(_.id == collectionFile.id).fold("")(_.name) + "/others/" + collectionFile.fileName
        val destinationKey = collectionFile.id + "/others/" + collectionFile.fileName
        if (!utilities.AmazonS3.exists(destinationKey)) {
          utilities.AmazonS3.copyObject(sourceKey = sourceKey, destinationKey = destinationKey)
        }
      } catch {
        case exception: Exception => logger.error(exception.getLocalizedMessage)
      }
    }

    for {
      collectionFiles <- collectionFiles
      collections <- collections
    } yield update(collectionFiles, collections)

  }

  private def updateCollectionNFTAwsFiles() = {
    val collections = masterCollections.Service.fetchAll()

    def update(collections: Seq[Collection]) = utilitiesOperations.traverse(collections) { collection =>
      val nfts = masterNFTs.Service.getAllForCollection(collection.id)

      for {
        nfts <- nfts
      } yield {
        nfts.foreach { nft =>
          try {
            val sourceKey = collections.find(_.id == nft.collectionId).fold("")(_.name) + "/nfts/" + nft.fileName
            val destinationKey = nft.collectionId + "/nfts/" + nft.fileName
            //            println(destinationKey)
            if (!utilities.AmazonS3.exists(destinationKey)) {
              utilities.AmazonS3.copyObject(sourceKey = sourceKey, destinationKey = destinationKey)
            }
          } catch {
            case exception: Exception => logger.error(exception.getLocalizedMessage)
          }
        }
      }
    }

    for {
      collections <- collections
    } yield update(collections)

  }

  private def updateNFTProperties() = {
    val collections = masterCollections.Service.fetchAll()

    def update(collections: Seq[Collection]) = utilitiesOperations.traverse(collections) { collection =>
      println(collection.name)
      println(collection.id)
      val nfts = masterNFTs.Service.getAllForCollection(collection.id)

      def updateNFTs(nfts: Seq[master.NFT]) = {
        val updateCollectionProperties = masterCollections.Service.updateById(collection.copy(properties = Option(nfts.head.properties.map(_.toBaseNFTProperty).map(x => models.common.Collection.Property(name = x.name, `type` = x.`type`, defaultValue = "")))))
        val nftsUpdate = utilitiesOperations.traverse(nfts)(nft => masterNFTProperties.Service.addMultiple(nft.properties.map(_.toBaseNFTProperty.toNFTProperty(nft.fileName))))
        for {
          _ <- updateCollectionProperties
          _ <- nftsUpdate
        } yield ()
      }

      for {
        nfts <- nfts
        _ <- updateNFTs(nfts)
      } yield ()
    }


    for {
      collections <- collections
      _ <- update(collections)
    } yield ()
  }

  private def updateSocialURLsAndCollectionFiles() = {
    val collections = masterCollections.Service.fetchAll()
    val collectionFiles = masterCollectionFiles.Service.fetchAll()

    def update(collections: Seq[Collection], collectionFiles: Seq[CollectionFile]) = utilitiesOperations.traverse(collections) { collection =>
      masterCollections.Service.updateById(collection.copy(
        socialProfiles = if (collection.website == "") collection.socialProfiles.map(x => x.copy(url = x.url.split("/").last)) else SocialProfile(name = constants.Collection.SocialProfile.WEBSITE, url = collection.website) +: collection.socialProfiles.map(x => x.copy(url = x.url.split("/").last)),
        profileFileName = collectionFiles.find(x => x.id == collection.id && x.documentType == constants.Collection.File.PROFILE).map(_.fileName),
        coverFileName = collectionFiles.find(x => x.id == collection.id && x.documentType == constants.Collection.File.COVER).map(_.fileName),
        website = "",
      ))
    }

    for {
      collections <- collections
      collectionFiles <- collectionFiles
      _ <- update(collections, collectionFiles)
    } yield ()
  }

  private def updateAccountType() = {
    val collections = masterCollections.Service.fetchAllPublic()

    def update(collections: Seq[Collection]) = utilitiesOperations.traverse(collections) { collection =>
      masterAccounts.Service.updateAccountType(accountId = collection.creatorId, accountType = constants.Account.Type.GENESIS_CREATOR)
    }

    for {
      collections <- collections
      _ <- update(collections)
    } yield ()
  }

  def start(): Future[Unit] = {

    //    (for {
    //      _ <- updateAccountType()
    //      _ <- updateNFTProperties()
    //      _ <- updateCollectionAwsFiles()
    //      _ <- updateCollectionNFTAwsFiles()
    //      _ <- updateSocialURLsAndCollectionFiles()
    //    } yield ()
    //      ).recover {
    //      case exception: Exception => logger.error(exception.getLocalizedMessage)
    //    }
    Future()
  }

}