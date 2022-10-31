package constants

import play.api.i18n.{Messages, MessagesProvider}

import scala.util.matching.Regex

case class RegularExpression(name: String, regex: Regex) {
  def getRegExErrorMessage()(implicit messagesProvider: MessagesProvider): String = Messages("REGEX_ERROR." + name)
}

object RegularExpression {
  val ANY_STRING: RegularExpression = RegularExpression("ANY_STRING", """.*""".r)
  val ALL_NUMBERS_ALL_LETTERS: RegularExpression = RegularExpression("ALL_NUMBERS_ALL_LETTERS", """^[A-Za-z0-9]*$""".r)
  val ALL_NUMBERS_ALL_CAPITAL_LETTERS: RegularExpression = RegularExpression("ALL_NUMBERS_ALL_CAPITAL_LETTERS", """^[A-Z0-9]*$""".r)
  val ALL_NUMBERS_ALL_SMALL_LETTERS: RegularExpression = RegularExpression("ALL_NUMBERS_ALL_SMALL_LETTERS", """^[a-z0-9]*$""".r)
  val MANTLE_ADDRESS: RegularExpression = RegularExpression("MANTLE_ADDRESS", """^mantle[a-z0-9]{39}$""".r)
  val ALL_SMALL_LETTERS: RegularExpression = RegularExpression("ALL_SMALL_LETTERS", """^[a-z]*$""".r)
  val ALL_SMALL_LETTERS_WITH_SPACE: RegularExpression = RegularExpression("ALL_SMALL_LETTERS_WITH_SPACE", """^[a-z ]*$""".r)
  val PASSWORD: RegularExpression = RegularExpression("PASSWORD", """^[A-Za-z0-9!@#$%^&*._-]*$""".r)
  val ACCOUNT_ID: RegularExpression = RegularExpression("ACCOUNT_ID", """^[a-zA-Z0-9!@#$._-]*$""".r)
  val WHITELIST_NAME: RegularExpression = RegularExpression("WHITELIST_NAME", """^[a-zA-Z0-9!@#$ ._-]*$""".r)
  val MOBILE_NUMBER: RegularExpression = RegularExpression("MOBILE_NUMBER", """^(\+\d{1,3}[- ]?)?\d{6,14}$""".r)
  val ALL_LETTERS: RegularExpression = RegularExpression("ALL_LETTERS", """^[a-zA-z]*$""".r)
  val HASH: RegularExpression = RegularExpression("HASH", """^[a-fA-F0-9]*$""".r)
  val EMAIL_ADDRESS: RegularExpression = RegularExpression("EMAIL_ADDRESS", """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r)
  val SWIFT_CODE: RegularExpression = RegularExpression("SWIFT_CODE", """^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$""".r)
  val SPLIT: RegularExpression = RegularExpression("SPLIT", """^[0-9]+(\\.[0-9]+)?$""".r)
  val TRANSACTION_HASH: RegularExpression = RegularExpression("TRANSACTION_HASH", """[A-F0-9]{64}""".r)
  val TWITTER_USERNAME: RegularExpression = RegularExpression("TWITTER_USERNAME", """^[A-Za-z0-9_]*$""".r)
  val INSTAGRAM_USERNAME: RegularExpression = RegularExpression("INSTAGRAM_USERNAME", """^[A-Za-z0-9._]*$""".r)
}
