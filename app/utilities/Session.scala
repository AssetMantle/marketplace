package utilities

import play.api.mvc.RequestHeader

object Session {

  def getSessionCachingKey(request: RequestHeader): String = {
    Seq(
      request.uri,
      request.session.get(constants.Session.USERNAME).getOrElse(""),
      request.session.get(constants.Session.TOKEN).getOrElse(""),
    ).mkString("+")
  }
}
