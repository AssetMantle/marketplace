package constants

import controllers.routes
import play.api.mvc.Call
import play.api.routing.JavaScriptReverseRoute

class Form(name: String, val get: JavaScriptReverseRoute, val post: Call) {
  val title: String = Seq("FORM", name, "TITLE").mkString(".")
  val submit: String = Seq("FORM", name, "SUBMIT").mkString(".")
}

object Form {
  val SIGN_UP = new Form("SIGN_UP", routes.javascript.AccountController.signUpForm, routes.AccountController.signUp)
}
