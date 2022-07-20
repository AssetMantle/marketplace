package controllers

import play.api.mvc._
import play.api.routing._
import play.api.{Configuration, Logger}

import javax.inject.{Inject, Singleton}

@Singleton
class JavaScriptRoutesController @Inject()(messagesControllerComponents: MessagesControllerComponents)(implicit configuration: Configuration) extends AbstractController(messagesControllerComponents) {

  private implicit val logger: Logger = Logger(this.getClass)

  def javascriptRoutes: Action[AnyContent] = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.Assets.versioned,

        routes.javascript.IndexController.index,

        routes.javascript.AccountController.signUpForm,
        routes.javascript.AccountController.verifyWalletMnemonicsForm,
        routes.javascript.AccountController.checkUsernameAvailable,
        routes.javascript.AccountController.signInForm,
        routes.javascript.AccountController.signOutForm,
        routes.javascript.AccountController.forgetPasswordForm,

        routes.javascript.CollectionController.viewCollections,
        routes.javascript.CollectionController.viewCollection,
        routes.javascript.CollectionController.collectionsList,
        routes.javascript.CollectionController.collectionsPerPage,
        routes.javascript.CollectionController.collectionFile,
        routes.javascript.CollectionController.collectionNFTs,
        routes.javascript.CollectionController.info,

        routes.javascript.NFTController.viewNFT,
        routes.javascript.NFTController.info,
        routes.javascript.NFTController.details,
        routes.javascript.NFTController.file,

        routes.javascript.ProfileController.viewSettings,
        routes.javascript.ProfileController.settingsPage,
        routes.javascript.ProfileController.addNewWallet,
        routes.javascript.ProfileController.managedAddressForm,
        routes.javascript.ProfileController.managedAddress,
        routes.javascript.ProfileController.unmanagedAddressForm,
        routes.javascript.ProfileController.unmanagedAddress,
        routes.javascript.ProfileController.changeWalletNameForm,
        routes.javascript.ProfileController.changeWalletName,

      )
    ).as("text/javascript")

  }
}
