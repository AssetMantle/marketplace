package constants

import schema.id.base._

object Transaction {

  val LowGasPrice: Double = CommonConfig.Blockchain.LowGasPrice
  val MediumGasPrice: Double = CommonConfig.Blockchain.MediumGasPrice
  val HighGasPrice: Double = CommonConfig.Blockchain.HighGasPrice
  val IdentityClassificationID: ClassificationID = ClassificationID(utilities.Secrets.base64URLDecode("IoTaHkXLe_NVFxz11-BhmxQZZX52EfmuAq5QM6DBR3k="))
  val FromID: IdentityID = IdentityID(utilities.Secrets.base64URLDecode("MuFGjnQuCNHHVP7u6HfAJ3tqd3Yc-EpOqT2IT4QetdU="))
  val OrderClassificationID: ClassificationID = ClassificationID(utilities.Secrets.base64URLDecode("9IqAiL2idrARX91QJZVBj89zjQN_y3_3sHc90z1DPTU="))
  // TODO BondRate from parameters
  val BondRate = 1
  val MaxOrderExpiry: Int = 43210
  val MaxOrderHours = 72

}
