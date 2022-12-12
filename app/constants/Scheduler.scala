package constants

import scala.concurrent.duration.{Duration, DurationInt, FiniteDuration, SECONDS}

object Scheduler {
  val InitialDelay: FiniteDuration = constants.CommonConfig.initialDelay.millis
  val FixedDelay: FiniteDuration = constants.CommonConfig.fixedDelay.millis
  val HalfDay: FiniteDuration = Duration.create(43200, SECONDS)

}
