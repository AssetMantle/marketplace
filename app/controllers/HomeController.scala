package controllers

import javax.inject._
import play.api._
import play.api.i18n.I18nSupport
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with I18nSupport {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def signup() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.SignUp())
  }

//  def createWallet() = Action { implicit request: Request[AnyContent] =>
//    Ok(views.html.createWallet())
//  }

  def seedPhraseVerification() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.seedPhraseVerification())
  }

  def assetMantleWalletSuccessfull() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.assetMantleWalletSuccessfull())
  }

//  def connectConfirmation() = Action { implicit request: Request[AnyContent] =>
//    Ok(views.html.connectConfirmation())
//  }

  def connectConfirmationRedirectToKeplrn() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.connectConfirmationRedirectToKeplr())
  }

//  def connectedSuccessToKeplr() = Action { implicit request: Request[AnyContent] =>
//    Ok(views.html.connectedSuccessToKeplr())
//  }

//  def importWalletSuccess() = Action { implicit request: Request[AnyContent] =>
//    Ok(views.html.importWalletSuccess())
//  }

  def signUpForm() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.signUpForm())
  }

  def signIn() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.signIn())
  }

  def signUpCompleteConnectedWallet() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.signUpCompleteConnectedWallet())
  }

  def forgotPasswordUsername() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.forgotPasswordUsername())
  }

  def forgotPasswordConfirmPassword() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.forgotPasswordConfirmPassword())
  }

  def resetPasswordStep1() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.resetPasswordStep1())
  }

  def resetPasswordStep2() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.resetPasswordStep2())
  }

  def resetPasswordStep3() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.resetPasswordStep3())
  }

  def profileMyNFTs() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.profile.profileMyNFTs())
  }

  def selectCollection() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.createCollection.selectCollection())
  }

  def createNewCollection() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.createCollection.createNewCollection())
  }

  def createNewCollectionSuccessful() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.createCollection.createNewCollectionSuccessful())
  }

  def createNewCollectionSaveDraft() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.createCollection.createNewCollectionSaveDraft())
  }

  def createNewCollectionDraftSaved() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.createCollection.createNewCollectionDraftSaved())
  }

  def createNewNFT() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.createCollection.createNewNFT())
  }

  def NFTCreatedSuccess() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.createCollection.NFTCreatedSuccess())
  }

  def mintNFT() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.createCollection.mintNFT())
  }

  def mintNFTSuccess() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.createCollection.mintNFTSuccess())
  }

  // Home
  def marketPlaceHome() = Action { implicit request: Request[AnyContent] => Ok(views.html.Home.marketPlaceHome()) }
  def home() = Action { implicit request: Request[AnyContent] => Ok(views.html.Home.homeTest()) }

  // SignIn-SignUp
  // Option 1: Connect Wallet
  def testSignUp() = Action { implicit request: Request[AnyContent] => Ok(views.html.signInSignUp.signUpTest()) }
  def connectWallet() = Action { implicit request: Request[AnyContent] => Ok(views.html.signInSignUp.connectWallet()) }
  def connectConfirmation() = Action { implicit request: Request[AnyContent] => Ok(views.html.signInSignUp.connectConfirmation()) }
  def redirectingToKeplr() = Action { implicit request: Request[AnyContent] => Ok(views.html.signInSignUp.redirectingToKeplr()) }
  def connectedSuccessToKeplr() = Action { implicit request: Request[AnyContent] => Ok(views.html.signInSignUp.connectedSuccessToKeplr()) }
  // Option 2: Import Wallet
  def importWalletSuccess() = Action { implicit request: Request[AnyContent] => Ok(views.html.signInSignUp.importWalletSuccess()) }
  // Option 3: Create AM Wallet
  def createWallet() = Action { implicit request: Request[AnyContent] => Ok(views.html.signInSignUp.createWallet()) }
}
