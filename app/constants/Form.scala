package constants

import controllers.routes
import play.api.mvc.Call
import play.api.routing.JavaScriptReverseRoute

import scala.collection.Seq

class Form(template: String, val route: Call, val get: JavaScriptReverseRoute) {
  val legend: String = Seq("FORM", template, "LEGEND").mkString(".")
  val submit: String = Seq("FORM", template, "SUBMIT").mkString(".")
  val button: String = Seq("FORM", template, "BUTTON").mkString(".")
}

object Form {
  val SIGN_UP = new Form("SIGN_UP", routes.HomeController.signIn(), routes.javascript.HomeController.signIn)
}