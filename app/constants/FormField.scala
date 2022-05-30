package constants

import play.api.data.Forms.text
import play.api.data.Mapping
import play.api.data.validation.Constraints

import scala.util.matching.Regex

object FormField {
  val USERNAME = new StringFormField("username",4, 10)
  val PASSWORD = new StringFormField("PASSWORD",6,20,RegularExpression.PASSWORD)
  val CONFIRM_PASSWORD = new StringFormField("CONFIRM_PASSWORD",6,20,RegularExpression.PASSWORD)
  val NAME = new StringFormField("NAME",4,40)

  class StringFormField(fieldName: String, minimumLength: Int, maximumLength: Int, regex: Regex = RegularExpression.ANY_STRING, errorMessage: String = "Error Response") {
    val name: String = fieldName
    val field: Mapping[String] = text(minLength = minimumLength, maxLength = maximumLength).verifying(Constraints.pattern(regex = regex, name = regex.pattern.toString, error = errorMessage))
  }

  class NestedFormField(fieldName: String) {
    val name: String = fieldName
  }
}