package utilities

import com.google.common.collect
import com.google.common.collect.ImmutableList
import exceptions.BaseException
import org.bitcoinj.core.{ECKey, Sha256Hash, Utils}
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.script.Script
import org.bitcoinj.wallet.{DeterministicSeed, Wallet => bitcoinjWallet}
import org.bouncycastle.jcajce.provider.digest.Keccak
import org.bouncycastle.jce.provider.BouncyCastleProvider
import play.api.Logger
import scodec.bits.ByteVector

import java.nio.charset.StandardCharsets
import java.security.{MessageDigest, Security}

case class Wallet(address: String, hdPath: Seq[ChildNumber], publicKey: Array[Byte], privateKey: Array[Byte], mnemonics: Seq[String])

object Wallet {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.UTILITIES_WALLET

  object BouncyHash {
    if (Security.getProvider("BC") == null) {
      Security.addProvider(new BouncyCastleProvider())
    }

    def sha256: MessageDigest = MessageDigest.getInstance("SHA-256", "BC")

    def ripemd160: MessageDigest = MessageDigest.getInstance("RipeMD160", "BC")

    def kec256(bytes: ByteVector): Array[Byte] = new Keccak.Digest256().digest(bytes.toArray)

    def kec512(bytes: ByteVector): Array[Byte] = new Keccak.Digest512().digest(bytes.toArray)
  }

  def getHDPath(account: Int, addressIndex: Int): ImmutableList[ChildNumber] = {
    collect.ImmutableList.of(
      new ChildNumber(44, true),
      new ChildNumber(constants.Blockchain.CoinType, true),
      new ChildNumber(account, true),
      new ChildNumber(0, false),
      new ChildNumber(addressIndex, false)
    )
  }

  def getWallet(mnemonics: Seq[String], hdPath: Seq[ChildNumber] = constants.Blockchain.DefaultHDPath, bip39Passphrase: Option[String] = None): Wallet = {
    val words = mnemonics.mkString(" ")
    val hdPathAsList = collect.ImmutableList.copyOf(scala.jdk.CollectionConverters.SeqHasAsJava(hdPath).asJava)
    if (Bip39.validate(mnemonics)) {
      val bitcoinWallet = bitcoinjWallet.fromSeed(
        MainNetParams.get(),
        new DeterministicSeed(words, Bip39.toSeed(words, bip39Passphrase), "", System.currentTimeMillis()),
        Script.ScriptType.P2PKH,
        hdPathAsList
      )

      val address = utilities.Bech32.encode(constants.Blockchain.AccountPrefix, utilities.Bech32.to5Bit(BouncyHash.ripemd160.digest(MessageDigest.getInstance("SHA-256").digest(bitcoinWallet.getKeyByPath(hdPathAsList).getPubKey))))
      Wallet(
        address = address,
        hdPath = hdPath,
        publicKey = bitcoinWallet.getKeyByPath(hdPathAsList).getPubKey,
        privateKey = bitcoinWallet.getKeyByPath(hdPathAsList).getPrivKeyBytes,
        mnemonics = mnemonics)
    } else throw new BaseException(constants.Response.INVALID_MNEMONICS)
  }

  def getRandomWallet: Wallet = getWallet(Bip39.creatRandomMnemonics())

  def hashAndEcdsaSign(message: String, ecKey: ECKey): Array[Byte] = {
    val ecdsaSignature = ecKey.sign(Sha256Hash.wrap(utilities.Secrets.sha256Hash(message.getBytes(StandardCharsets.UTF_8))))
    Utils.bigIntegerToBytes(ecdsaSignature.r, 32) ++ Utils.bigIntegerToBytes(ecdsaSignature.s, 32)
  }

  def ecdsaSign(data: Array[Byte], ecKey: ECKey): Array[Byte] = try {
    val ecdsaSignature = ecKey.sign(Sha256Hash.wrap(data))
    Utils.bigIntegerToBytes(ecdsaSignature.r, 32) ++ Utils.bigIntegerToBytes(ecdsaSignature.s, 32)
  } catch {
    case exception: Exception => constants.Response.SIGNING_FAILED.throwBaseException(exception)
  }

}
