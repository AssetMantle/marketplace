package utilities

import exceptions.BaseException
import models.common.Coin
import org.bouncycastle.asn1.sec.SECNamedCurves
import org.bouncycastle.crypto.params.{ECDomainParameters, ECPublicKeyParameters}
import org.bouncycastle.crypto.signers.ECDSASigner
import play.api.Logger

import java.math.BigInteger

object Blockchain {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.UTILITIES_BLOCKCHAIN

  def addCoins(oldCoins: Seq[Coin], add: Seq[Coin]): Seq[Coin] = if (oldCoins.nonEmpty) {
    val newCoins = oldCoins.map(oldCoin => add.find(_.denom == oldCoin.denom).fold(oldCoin)(addCoin => Coin(denom = addCoin.denom, amount = oldCoin.amount + addCoin.amount)))
    newCoins ++ add.filter(x => !newCoins.map(_.denom).contains(x.denom))
  } else add

  def subtractCoins(fromCoins: Seq[Coin], amount: Seq[Coin]): (Seq[Coin], Boolean) = {
    val result = addCoins(fromCoins, amount.map(x => x.copy(amount = x.amount * -1)))
    (result, result.exists(_.isNegative == true))
  }

}
