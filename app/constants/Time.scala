package constants

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.{Duration, FiniteDuration}

object Time {
  val Infinity: Duration.Infinite = Duration.Inf
  val OneSecond = new FiniteDuration(1, TimeUnit.SECONDS)
  val FiveSecond = new FiniteDuration(5, TimeUnit.SECONDS)
  val SixSecond = new FiniteDuration(6, TimeUnit.SECONDS)
  val OneDay = new FiniteDuration(86400, TimeUnit.SECONDS)
  val HalfDay = new FiniteDuration(43200, TimeUnit.SECONDS)

  def getSeconds(n: Long) = new FiniteDuration(n, TimeUnit.SECONDS)
}
