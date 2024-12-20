package controllers.actions

case class LoginState(username: String, address: String, accountType: String) {

  def isCreator: Boolean = utilities.Account.isCreator(this.accountType)

  def isVerifiedCreator: Boolean = utilities.Account.isVerifiedCreator(this.accountType)

  def isGenesisCreator: Boolean = this.accountType == constants.Account.Type.GENESIS_CREATOR

  def isUser: Boolean = this.accountType == constants.Account.Type.USER

  // TODO
  def getIdentityId: String = ""

}
