package controllers

import akka.stream.scaladsl.{Source, StreamConverters}
import akka.util.ByteString
import controllers.actions.{WithLoginActionAsync, WithoutLoginAction, WithoutLoginActionAsync}
import exceptions.BaseException
import models.master
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, Action, AnyContent, EssentialAction, MessagesControllerComponents}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class NFTController @Inject()(
                               messagesControllerComponents: MessagesControllerComponents,
                               cached: Cached,
                               withoutLoginActionAsync: WithoutLoginActionAsync,
                               withLoginActionAsync: WithLoginActionAsync,
                               withoutLoginAction: WithoutLoginAction,
                               masterAccounts: master.Accounts,
                               masterWallets: master.Wallets,
                               masterCollections: master.Collections,
                               masterNFTs: master.NFTs,
                             )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.NFT_CONTROLLER

  def viewNFT(nftId: String): EssentialAction = cached.apply(req => req.path + "/" + nftId, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val nft = masterNFTs.Service.tryGet(nftId)
        (for {
          nft <- nft
        } yield Ok(views.html.nft.viewNft(nft))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def details(nftId: String): EssentialAction = cached.apply(req => req.path + "/" + nftId, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val nft = masterNFTs.Service.tryGet(nftId)

        def getCollection(collectionID: String) = masterCollections.Service.tryGet(collectionID)

        (for {
          nft <- nft
          collection <- getCollection(nft.collectionId)
        } yield Ok(views.html.nft.details(collection, nft))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def info(nftId: String): EssentialAction = cached.apply(req => req.path + "/" + nftId, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val nft = masterNFTs.Service.tryGet(nftId)
        (for {
          nft <- nft
        } yield Ok(views.html.nft.info(nft))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def file(nftId: String): Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      val nft = masterNFTs.Service.tryGet(nftId)

      def getCollection(collectionID: String) = masterCollections.Service.tryGet(collectionID)

      (for {
        nft <- nft
        collection <- getCollection(nft.collectionId)
      } yield {
        val source: Source[ByteString, _] = StreamConverters.fromInputStream(() => utilities.AmazonS3.getFullObject(collection.name + "/nfts/" + nft.fileName).getObjectContent.getDelegateStream)
        Ok.chunked(source, inline = true, Option(nft.fileName))
      }
        ).recover {
        case baseException: BaseException => InternalServerError(baseException.failure.message)
      }
  }

}