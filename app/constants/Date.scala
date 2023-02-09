package constants

object Date {

  val HourSeconds: Long = (60 * 60).toLong
  val HalfDaySeconds: Long = 12 * HourSeconds
  val HalfDayMilliSeconds: Long = HalfDaySeconds * 1000
  val DaySeconds: Long = 24 * HourSeconds
  val DayMilliSeconds: Long = DaySeconds * 1000
  val ThreeDayMilliSeconds: Long = 3 * DayMilliSeconds
}
