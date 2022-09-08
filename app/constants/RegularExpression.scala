package constants

import scala.util.matching.Regex

object RegularExpression {
  val ANY_STRING: Regex = """.*""".r
  val ALL_NUMBERS_ALL_LETTERS: Regex = """^[A-Za-z0-9]*$""".r
  val ALL_NUMBERS_ALL_CAPITAL_LETTERS: Regex = """^[A-Z0-9]*$""".r
  val ALL_NUMBERS_ALL_SMALL_LETTERS: Regex = """^[a-z0-9]*$""".r
  val MANTLE_ADDRESS: Regex = """^mantle[a-z0-9]{39}$""".r
  val ALL_SMALL_LETTERS: Regex = """^[a-z]*$""".r
  val ALL_SMALL_LETTERS_WITH_SPACE: Regex = """^[a-z ]*$""".r
  val PASSWORD: Regex = """^[A-Za-z0-9!@#$%^&*._-]*$""".r
  val ACCOUNT_ID: Regex = """^[a-zA-Z0-9!@#$._-]*$""".r
  val WHITE_LIST_NAME: Regex = """^[a-zA-Z0-9!@#$ ._-]*$""".r
  val MOBILE_NUMBER: Regex = """^(\+\d{1,3}[- ]?)?\d{6,14}$""".r
  val PEG_HASH: Regex = """^[0-9]*$""".r
  val ALL_LETTERS: Regex = """^[a-zA-z]*$""".r
  val HASH: Regex = """^[a-fA-F0-9]*$""".r
  val EMAIL_ADDRESS: Regex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r
  val SWIFT_CODE: Regex = """^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$""".r
  val SPLIT: Regex = """^[0-9]+(\\.[0-9]+)?$""".r
  val TRANSACTION_HASH: Regex = """[A-F0-9]{64}""".r
}
