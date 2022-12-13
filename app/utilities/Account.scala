package utilities

object Account {

  def isCreator(accountType: String): Boolean = accountType == constants.Account.Type.CREATOR || accountType == constants.Account.Type.VERIFIED_CREATOR || accountType == constants.Account.Type.GENESIS_CREATOR || accountType == constants.Account.Type.FEATURED_CREATOR

  def isVerifiedCreator(accountType: String): Boolean = accountType == constants.Account.Type.VERIFIED_CREATOR || accountType == constants.Account.Type.GENESIS_CREATOR || accountType == constants.Account.Type.FEATURED_CREATOR

}
