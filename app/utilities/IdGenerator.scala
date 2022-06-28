package utilities

import scala.util.Random

object IdGenerator {

  def getRandomHexadecimal: String = (-Math.abs(Random.nextLong)).toHexString.toUpperCase
}
