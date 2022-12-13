package utilities

import models.master.Key
import models.masterTransaction.SessionToken
import org.joda.time.{DateTime, DateTimeZone}
import play.api.mvc.RequestHeader

object Session {

  def getSessionCachingKey(request: RequestHeader, cacheWithUsername: Boolean = true): String = if (cacheWithUsername) {
    Seq(
      request.uri,
      request.session.get(constants.Session.USERNAME).getOrElse(""),
      request.session.get(constants.Session.TOKEN).getOrElse(""),
    ).mkString("+")
  } else request.uri

  def verify(username: String, key: Key, address: String, storedSessionToken: SessionToken, currentSessionToken: String): Boolean = {
    key.accountId == username && key.address == address && storedSessionToken.sessionTokenHash == utilities.Secrets.sha256HashString(currentSessionToken) && (DateTime.now(DateTimeZone.UTC).getMillis - storedSessionToken.sessionTokenTime < constants.CommonConfig.SessionTokenTimeout)
  }
}
