package constants

import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import views.account.companion._

object FormConstraint {

  val signUpConstraint: Constraint[SignUp.Data] = Constraint("constraints.SignUp")({ signUpData: SignUp.Data =>
    val errors = {
      val passwordSymbols = "!@#$%^&*._-"
      if (signUpData.password != signUpData.confirmPassword) Seq(ValidationError(constants.Response.PASSWORDS_DO_NOT_MATCH.message))
      else if (!signUpData.usernameAvailable) Seq(ValidationError(constants.Response.USERNAME_UNAVAILABLE.message))
      else if (!signUpData.password.exists(_.isLower) || !signUpData.password.exists(_.isUpper) || !signUpData.password.exists(_.isDigit) || !signUpData.password.exists(passwordSymbols.contains(_))) Seq(ValidationError(constants.Response.PASSWORD_VALIDATION_FAILED.message))
      else Nil
    }
    if (errors.isEmpty) Valid else Invalid(errors)
  })

  val forgotPasswordConstraint: Constraint[ForgotPassword.Data] = Constraint("constraints.ForgotPassword")({ forgotPasswordData: ForgotPassword.Data =>
    val errors = {
      if (forgotPasswordData.newPassword != forgotPasswordData.confirmNewPassword) Seq(ValidationError(constants.Response.PASSWORDS_DO_NOT_MATCH.message))
      else Nil
    }
    if (errors.isEmpty) Valid else Invalid(errors)
  })

}
