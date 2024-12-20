package constants

import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import views.account.companion._
import views.blockchainTransaction.companion._
import views.collection.{companion => collection}
import views.nft.companion._
import views.profile.whitelist.{companion => whitelist}
import views.publicListing.{companion => publicListing}
import views.sale.{companion => sale}
import views.setting.companion._

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
    val seeds = addManagedKeyData.seeds.split(" ")
    val walletError = try {
      val wallet = utilities.Wallet.getWallet(seeds)
      if (wallet.address != addManagedKeyData.address) true else false
    } catch {
      case _: Exception => true
    }
    val errors = Seq(
      if (!(seeds.length == 24 || seeds.length == 12)) Option(ValidationError(constants.Response.MNEMONICS_LENGTH_NOT_12_OR_24.message)) else None,
      if (walletError) Option(ValidationError(constants.Response.INVALID_SEEDS_OR_ADDRESS.message)) else None,
    ).flatten
    if (errors.isEmpty) Valid else Invalid(errors)
  })

  val addUnmanagedKeyConstraint: Constraint[AddUnmanagedKey.Data] = Constraint("constraints.AddUnmanagedKey")({ addUnmanagedKeyData: AddUnmanagedKey.Data =>
    val errors = Seq(
      if (!addUnmanagedKeyData.address.startsWith("mantle") || addUnmanagedKeyData.address.length != 45) Option(ValidationError(constants.Response.INVALID_WALLET_ADDRESS.message)) else None,
    ).flatten
    if (errors.isEmpty) Valid else Invalid(errors)
  })

  val sendCoinConstraint: Constraint[SendCoin.Data] = Constraint("constraints.SendCoin")({ sendCoin: SendCoin.Data =>
    val errors = Seq(
      if (!sendCoin.fromAddress.startsWith("mantle") || sendCoin.fromAddress.length != 45) Option(ValidationError(constants.Response.INVALID_FROM_ADDRESS.message)) else None,
      if (!sendCoin.toAddress.startsWith("mantle") || sendCoin.toAddress.length != 45) Option(ValidationError(constants.Response.INVALID_TO_ADDRESS.message)) else None,
      if (sendCoin.fromAddress == sendCoin.toAddress) Option(ValidationError(constants.Response.FROM_AND_TO_ADDRESS_SAME.message)) else None,
    ).flatten
    if (errors.isEmpty) Valid else Invalid(errors)
  })

  val createWhitelistInviteConstraint: Constraint[whitelist.Create.Data] = Constraint("constraints.CreateWhitelist")({ createWhitelistData: whitelist.Create.Data =>
    val errors = Seq(
      if (createWhitelistData.startEpoch >= createWhitelistData.endEpoch) Option(ValidationError(constants.Response.START_TIME_GREATER_THAN_EQUAL_TO_END_TIME.message)) else None,
      if (createWhitelistData.startEpoch < (utilities.Date.currentEpoch - 1800)) Option(ValidationError(constants.Response.START_TIME_LESS_THAN_CURRENT_TIME.message)) else None,
    ).flatten
    if (errors.isEmpty) Valid else Invalid(errors)
  })

  val editWhitelistInviteConstraint: Constraint[whitelist.Edit.Data] = Constraint("constraints.EditWhitelist")({ editWhitelistData: whitelist.Edit.Data =>
    val errors = Seq(
      if (editWhitelistData.startEpoch >= editWhitelistData.endEpoch) Option(ValidationError(constants.Response.START_TIME_GREATER_THAN_EQUAL_TO_END_TIME.message)) else None,
    ).flatten
    if (errors.isEmpty) Valid else Invalid(errors)
  })

  val createCollectionConstraint: Constraint[collection.Create.Data] = Constraint("constraints.CreateCollection")({ createData: collection.Create.Data =>
    val errors = Seq(
      if (!createData.termsAccepted) Option(ValidationError(constants.Response.TERMS_AND_CONDITION_NOT_ACCEPTED.message)) else None,
      if (!createData.mou) Option(ValidationError(constants.Response.MOU_NOT_ACCEPTED.message)) else None,
    ).flatten
    if (errors.isEmpty) Valid else Invalid(errors)
  })

  val collectionPropertiesConstraint: Constraint[collection.DefineProperties.Property] = Constraint("constraints.DefinePropertiesProperty")({ propertyData: collection.DefineProperties.Property =>
    val errors = Seq(
      if (!constants.NFT.Data.TypesList.contains(propertyData.propertyType)) Option(ValidationError(constants.Response.NFT_PROPERTY_TYPE_NOT_FOUND.message)) else None,
      if (propertyData.propertyType == constants.NFT.Data.BOOLEAN && propertyData.defaultValue.isDefined && !(propertyData.defaultValue.getOrElse("") == constants.NFT.Data.TRUE || propertyData.defaultValue.getOrElse("") == constants.NFT.Data.FALSE)) Option(ValidationError(constants.Response.INVALID_DEFAULT_VALUE.message)) else None,
      if (propertyData.propertyType == constants.NFT.Data.DECIMAL && propertyData.defaultValue.isDefined && propertyData.defaultValue.getOrElse("0").toDoubleOption.isEmpty) Option(ValidationError(constants.Response.INVALID_DEFAULT_VALUE.message)) else None,
    ).flatten
    if (errors.isEmpty) Valid else Invalid(errors)
  })

  val defineCollectionPropertiesConstraint: Constraint[collection.DefineProperties.Data] = Constraint("constraints.DefineCollectionPropertiesConstraint")({ definePropertiesData: collection.DefineProperties.Data =>
    val definedPropertiesNames = definePropertiesData.properties.flatMap(_.name)
    val errors = Seq(
      if ((definedPropertiesNames.length + constants.Collection.DefaultProperty.list.length) > constants.Blockchain.MaximumProperties) Option(ValidationError(constants.Response.MAXIMUM_COLLECTION_PROPERTIES_EXCEEDED.message)) else None,
      if (definedPropertiesNames.map(_.toLowerCase).intersect(constants.Collection.DefaultProperty.list.map(_.toLowerCase)).nonEmpty) Option(ValidationError(constants.Response.COLLECTION_PROPERTIES_CONTAINS_DEFAULT_PROPERTIES.message)) else None,
      if (definedPropertiesNames.distinct.length != definedPropertiesNames.length) Option(ValidationError(constants.Response.COLLECTION_PROPERTIES_CONTAINS_DUPLICATE_PROPERTIES.message)) else None,
    ).flatten
    if (errors.isEmpty) Valid else Invalid(errors)
  })

  val nftTagsConstraint: Constraint[NFTTags.Data] = Constraint("constraints.NFTTagsConstraint")({ nftTagsData: NFTTags.Data =>
    if (nftTagsData.tags != "") {
      val tags = nftTagsData.tags.split(constants.NFT.Tags.Separator)
      val errors = Seq(
        if (tags.length > constants.NFT.Tags.MaximumAllowed) Option(ValidationError(constants.Response.MAXIMUM_NFT_TAGS_EXCEEDED.message)) else None,
        if (tags.exists(x => x.length < constants.NFT.Tags.MinimumLength || x.length > constants.NFT.Tags.MaximumLength)) Option(ValidationError(constants.Response.INVALID_NFT_TAGS_LENGTH.message)) else None,
        if (tags.distinct.length != tags.length) Option(ValidationError(constants.Response.REPEATED_NFT_TAGS.message)) else None,
        if (tags.distinct.length != tags.length) Option(ValidationError(constants.Response.REPEATED_NFT_TAGS.message)) else None,
      ).flatten
      if (errors.isEmpty) Valid else Invalid(errors)
    } else Valid
  })

  val createSale: Constraint[sale.CreateCollectionSale.Data] = Constraint("constraints.CreateCollectionSale")({ createCollectionSaleData: sale.CreateCollectionSale.Data =>
    val errors = Seq(
      if (createCollectionSaleData.startEpoch >= createCollectionSaleData.endEpoch) Option(ValidationError(constants.Response.START_TIME_GREATER_THAN_EQUAL_TO_END_TIME.message)) else None,
      if (createCollectionSaleData.startEpoch < (utilities.Date.currentEpoch - 1800)) Option(ValidationError(constants.Response.START_TIME_LESS_THAN_CURRENT_TIME.message)) else None,
    ).flatten
    if (errors.isEmpty) Valid else Invalid(errors)
  })

  val createPublicListing: Constraint[publicListing.CreatePublicListing.Data] = Constraint("constraints.CreatePublicListing")({ createPublicListingData: publicListing.CreatePublicListing.Data =>
    val errors = Seq(
      if (createPublicListingData.startEpoch >= createPublicListingData.endEpoch) Option(ValidationError(constants.Response.START_TIME_GREATER_THAN_EQUAL_TO_END_TIME.message)) else None,
      if (createPublicListingData.startEpoch < (utilities.Date.currentEpoch - 1800)) Option(ValidationError(constants.Response.START_TIME_LESS_THAN_CURRENT_TIME.message)) else None,
    ).flatten
    if (errors.isEmpty) Valid else Invalid(errors)
  })

  val editPublicListing: Constraint[publicListing.EditPublicListing.Data] = Constraint("constraints.EditPublicListing")({ editPublicListingData: publicListing.EditPublicListing.Data =>
    val errors = Seq(
      if (editPublicListingData.startEpoch >= editPublicListingData.endEpoch) Option(ValidationError(constants.Response.START_TIME_GREATER_THAN_EQUAL_TO_END_TIME.message)) else None,
      if (editPublicListingData.startEpoch < (utilities.Date.currentEpoch - 1800)) Option(ValidationError(constants.Response.START_TIME_LESS_THAN_CURRENT_TIME.message)) else None,
    ).flatten
    if (errors.isEmpty) Valid else Invalid(errors)
  })
}
