package views.collection.companion

import models.common.{Collection => commonCollection}
import play.api.data.Form
import play.api.data.Forms.mapping
import utilities.MicroNumber

object SetCapabilities {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.COLLECTION_ID.mapping,
      constants.FormField.COLLECTION_FRACTIONALIZED_ENABLE.mapping,
      constants.FormField.COLLECTION_FRACTIONALIZED_MUTABLE.mapping,
      constants.FormField.COLLECTION_FRACTIONALIZED_HIDDEN.mapping,
      constants.FormField.COLLECTION_FRACTIONALIZED_DEFAULT.optionalMapping,
      constants.FormField.COLLECTION_LOCKABLE_ENABLE.mapping,
      constants.FormField.COLLECTION_LOCKABLE_MUTABLE.mapping,
      constants.FormField.COLLECTION_LOCKABLE_HIDDEN.mapping,
      constants.FormField.COLLECTION_LOCKABLE_DEFAULT.optionalMapping,
      constants.FormField.COLLECTION_CUSTOM_BURNABLE_ENABLE.mapping,
      constants.FormField.COLLECTION_CUSTOM_BURNABLE_MUTABLE.mapping,
      constants.FormField.COLLECTION_CUSTOM_BURNABLE_HIDDEN.mapping,
      constants.FormField.COLLECTION_CUSTOM_BURNABLE_DEFAULT.optionalMapping,
      constants.FormField.COLLECTION_CUSTOM_BOND_AMOUNT_ENABLE.mapping,
      constants.FormField.COLLECTION_CUSTOM_BOND_AMOUNT_DEFAULT.optionalMapping,
    )(Data.apply)(Data.unapply))

  case class Data(
                   collectionId: String,

                   fractionalized: Boolean,
                   fractionalizedMutable: Boolean,
                   fractionalizedHidden: Boolean,
                   fractionalizedDefault: Option[Int],

                   lockable: Boolean,
                   lockableMutable: Boolean,
                   lockableHidden: Boolean,
                   lockableDefault: Option[Int],

                   customBurnable: Boolean,
                   customBurnableMutable: Boolean,
                   customBurnableHidden: Boolean,
                   customBurnableDefault: Option[Int],

                   customBondAmount: Boolean,
                   customBondAmountDefault: Option[MicroNumber],
                 ) {

    def getProperties: Seq[commonCollection.Property] = {
      var properties = Seq[commonCollection.Property]()
      if (this.fractionalized) properties = properties :+ commonCollection.Property(name = schema.constants.Properties.SupplyProperty.id.keyID.value, `type` = constants.NFT.Data.NUMBER, defaultValue = this.fractionalizedDefault.fold("")(_.toString), meta = !this.fractionalizedHidden, mutable = this.fractionalizedMutable)
      if (this.lockable) properties = properties :+ commonCollection.Property(name = schema.constants.Properties.LockHeightProperty.id.keyID.value, `type` = constants.NFT.Data.HEIGHT, defaultValue = this.lockableDefault.fold("")(_.toString), meta = !this.lockableHidden, mutable = this.lockableMutable)
      if (this.customBurnable) properties = properties :+ commonCollection.Property(name = schema.constants.Properties.BurnHeightProperty.id.keyID.value, `type` = constants.NFT.Data.HEIGHT, defaultValue = this.customBurnableDefault.fold("")(_.toString), meta = !this.customBurnableHidden, mutable = this.customBurnableMutable)
      if (this.customBondAmount) properties = properties :+ commonCollection.Property(name = schema.constants.Properties.BondAmountProperty.id.keyID.value, `type` = constants.NFT.Data.NUMBER, defaultValue = this.customBondAmountDefault.fold("")(_.value.toString()), meta = true, mutable = false)
      properties
    }
  }

}
