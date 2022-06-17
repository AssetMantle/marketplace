package controllers

import controllers.actions._
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class HomeController @Inject()(
                                messagesControllerComponents: MessagesControllerComponents,
                                cached: Cached,
                                withoutLoginActionAsync: WithoutLoginActionAsync,
                                withoutLoginAction: WithoutLoginAction
                              )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.HOME_CONTROLLER

  def index: EssentialAction = cached.apply(req => req.path, constants.CommonConfig.webAppCacheDuration) {
    withoutLoginAction { implicit request =>
      Ok(views.html.index())
    }
  }




  def selectCollection() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.collection.selectCollection())
  }

//  def createNewCollection() = Action { implicit request: Request[AnyContent] =>
//    Ok(views.html.collection.createNewCollection())
//  }

  def createNewCollectionSuccessful() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.collection.createNewCollectionSuccessful())
  }

  def createNewCollectionSaveDraft() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.collection.createNewCollectionSaveDraft())
  }

  def createNewCollectionDraftSaved() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.collection.createNewCollectionDraftSaved())
  }

  def createNewNFT() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.collection.createNewNFT())
  }

  def NFTCreatedSuccess() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.collection.NFTCreatedSuccess())
  }

  def mintNFT() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.collection.mintNFT())
  }

  def mintNFTSuccess() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.collection.mintNFTSuccess())
  }

  // Home
  def marketPlaceHome() = Action { implicit request: Request[AnyContent] => Ok(views.html.home.marketPlaceHome()) }
  def home() = Action { implicit request: Request[AnyContent] => Ok(views.html.home.homeTest()) }

  // SignIn-SignUp
  // Option 1: Connect Wallet
  def connectWallet() = Action { implicit request: Request[AnyContent] => Ok(views.html.signInSignUp.connectWallet()) }
  def connectConfirmation() = Action { implicit request: Request[AnyContent] => Ok(views.html.signInSignUp.connectConfirmation()) }
  def redirectingToKeplr() = Action { implicit request: Request[AnyContent] => Ok(views.html.signInSignUp.redirectingToKeplr()) }
  def connectedSuccessToKeplr() = Action { implicit request: Request[AnyContent] => Ok(views.html.signInSignUp.connectedSuccessToKeplr()) }

  // Option 2: Import Wallet
  def importWalletSuccess() = Action { implicit request: Request[AnyContent] => Ok(views.html.signInSignUp.importWalletSuccess()) }

  // Option 3: Create AM Wallet
  def createWallet() = Action { implicit request: Request[AnyContent] => Ok(views.html.signInSignUp.createWallet()) }
  def seedPhraseVerification() = Action { implicit request: Request[AnyContent] => Ok(views.html.signInSignUp.seedPhraseVerification()) }
  def assetMantleWalletSuccessfull() = Action { implicit request: Request[AnyContent] => Ok(views.html.signInSignUp.assetMantleWalletSuccessfull()) }
  def signUpMantleplaceCredentials() = Action { implicit request: Request[AnyContent] => Ok(views.html.signInSignUp.signUpMantleplaceCredentials()) }

  // Create Collection & NFT
  def createNewCollection() = Action { implicit request: Request[AnyContent] => Ok(views.html.collection.createNewCollection()) }

  // Profile
  // Visitor's POV
  def creatorsProfile() = Action { implicit request: Request[AnyContent] => Ok(views.html.userProfile.creatorProfileBootstrap()) }
  def creatorsProfile2() = Action { implicit request: Request[AnyContent] => Ok(views.html.userProfile.creatorsProfile()) }
}
