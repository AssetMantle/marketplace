package constants

import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import views.account.companion._
import views.profile.companion._

object FormConstraint {
  val passwordSymbols = "!@#$%^&*._-"

  def verifyPasswordConstraints(password: String): Boolean = password.exists(_.isLower) && password.exists(_.isUpper) && password.exists(_.isDigit) && password.exists(passwordSymbols.contains(_))

  val signUpConstraint: Constraint[SignUp.Data] = Constraint("constraints.SignUp")({ signUpData: SignUp.Data =>
    val errors = Seq(
      if (!signUpData.termsCondition) Option(ValidationError(constants.Response.TERMS_AND_CONDITION_NOT_ACCEPTED.message)) else None,
      if (!signUpData.usernameAvailable) Option(ValidationError(constants.Response.USERNAME_UNAVAILABLE.message)) else None,
    ).flatten
    if (errors.isEmpty) Valid else Invalid(errors)
  })

  val verifyMnemonicsConstraint: Constraint[VerifyMnemonics.Data] = Constraint("constraints.VerifyMnemonics")({ verifyMnemonicsData: VerifyMnemonics.Data =>
    val errors = Seq(
      if (verifyMnemonicsData.password != verifyMnemonicsData.confirmPassword) Option(ValidationError(constants.Response.PASSWORDS_DO_NOT_MATCH.message)) else None,
      if (!verifyPasswordConstraints(verifyMnemonicsData.password)) Option(ValidationError(constants.Response.PASSWORD_VALIDATION_FAILED.message)) else None,
    ).flatten
    if (errors.isEmpty) Valid else Invalid(errors)
  })

  val forgotPasswordConstraint: Constraint[ForgotPassword.Data] = Constraint("constraints.ForgotPassword")({ forgotPasswordData: ForgotPassword.Data =>
    val errors = Seq(
      if (forgotPasswordData.newPassword != forgotPasswordData.confirmNewPassword) Option(ValidationError(constants.Response.PASSWORDS_DO_NOT_MATCH.message)) else None,
      if (!verifyPasswordConstraints(forgotPasswordData.newPassword)) Option(ValidationError(constants.Response.PASSWORD_VALIDATION_FAILED.message)) else None,
    ).flatten
    if (errors.isEmpty) Valid else Invalid(errors)
  })

  val changePasswordConstraint: Constraint[ChangePassword.Data] = Constraint("constraints.ChangePassword")({ changePasswordData: ChangePassword.Data =>
    val errors = Seq(
      if (changePasswordData.newPassword != changePasswordData.confirmNewPassword) Option(ValidationError(constants.Response.PASSWORDS_DO_NOT_MATCH.message)) else None,
      if (!verifyPasswordConstraints(changePasswordData.newPassword)) Option(ValidationError(constants.Response.PASSWORD_VALIDATION_FAILED.message)) else None,
      if (changePasswordData.newPassword == changePasswordData.oldPassword) Option(ValidationError(constants.Response.OLD_AND_NEW_SAME_PASSWORD.message)) else None,
    ).flatten
    if (errors.isEmpty) Valid else Invalid(errors)
  })

  val addManagedKeyConstraint: Constraint[AddManagedKey.Data] = Constraint("constraints.AddManagedKey")({ addManagedKeyData: AddManagedKey.Data =>
    val seeds =  addManagedKeyData.seeds.split(" ")
    val walletError = try {
      val wallet = utilities.Wallet.getWallet(seeds)
      if (wallet.address != addManagedKeyData.address) true else false
    } catch {
      case _: Exception => true
    }
    val errors = Seq(
      if (seeds.length != 24 || seeds.length != 12) Option(ValidationError(constants.Response.MNEMONICS_LENGTH_NOT_12_OR_24.message)) else None,
      if (walletError) Option(ValidationError(constants.Response.INVALID_SEEDS_OR_ADDRESS.message)) else None,
    ).flatten
    if (errors.isEmpty) Valid else Invalid(errors)
  })
}
