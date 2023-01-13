package constants

object PublicListing {

  case class Status(id: Int, value: String)

  val NOT_STARTED: Status = Status(0, "NOT_STARTED")
  val LIVE: Status = Status(1, "LIVE")
  val SOLD_OUT: Status = Status(2, "SOLD_OUT")
  val EXPIRED: Status = Status(3, "EXPIRED")

}
