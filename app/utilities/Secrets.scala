package utilities

import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.{MessageDigest, SecureRandom}
import java.util
import java.util.Base64
import javax.crypto.spec.{PBEKeySpec, SecretKeySpec}
import javax.crypto.{Cipher, SecretKeyFactory}

object Secrets {

  def encryptData(data: Array[Byte], secret: String): Array[Byte] = {
    val key = MessageDigest.getInstance("SHA-256").digest(secret.getBytes())
    val aesKey = new SecretKeySpec(util.Arrays.copyOf(key, 16), "AES")
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.ENCRYPT_MODE, aesKey)
    cipher.doFinal(data)
  }

  def decryptData(encryptedData: Array[Byte], secret: String): Array[Byte] = {
    val key = MessageDigest.getInstance("SHA-256").digest(secret.getBytes())
    val aesKey = new SecretKeySpec(util.Arrays.copyOf(key, 16), "AES")
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.DECRYPT_MODE, aesKey)
    cipher.doFinal(encryptedData)
  }

  def pbkdf2(password: String, salt: Array[Byte], iterations: Int): Array[Byte] = {
    val keySpec = new PBEKeySpec(password.toCharArray, salt, iterations, 256)
    val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    keyFactory.generateSecret(keySpec).getEncoded
  }

  def getNewSalt: Array[Byte] = {
    val salt = new Array[Byte](16)
    val random = new SecureRandom()
    random.nextBytes(salt)
    salt
  }

  def hashPassword(password: String, salt: Array[Byte], iterations: Int = constants.Security.DefaultIterations): Array[Byte] = {
    pbkdf2(password, salt, iterations)
  }

  def verifyPassword(password: String, passwordHash: Array[Byte], salt: Array[Byte], iterations: Int = constants.Security.DefaultIterations): Boolean = {
    pbkdf2(password, salt, iterations).sameElements(passwordHash)
  }

  def sha256Hash(value: String): Array[Byte] = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))

  def sha256HashString(value: String) : String = java.lang.String.format("%064x", new BigInteger(1, MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))))

}