package models.masterTransaction

import exceptions.BaseException
import models.Trait.{Entity, GenericDaoImpl, Logged, ModelTable}
import org.joda.time.{DateTime, DateTimeZone}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, ExecutionContext, Future}

case class SessionToken(accountId: String, sessionTokenHash: String, sessionTokenTime: Long, createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged with Entity[String] {
  def id: String = accountId
}

object SessionTokens {

  implicit val module: String = constants.Module.MASTER_TRANSACTION_SESSION_TOKEN

  implicit val logger: Logger = Logger(this.getClass)

  class SessionTokenTable(tag: Tag) extends Table[SessionToken](tag, "SessionToken") with ModelTable[String] {

    def * = (accountId, sessionTokenHash, sessionTokenTime, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (SessionToken.tupled, SessionToken.unapply)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def sessionTokenHash = column[String]("sessionTokenHash")

    def sessionTokenTime = column[Long]("sessionTokenTime")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

    override def id = accountId
  }

  val TableQuery = new TableQuery(tag => new SessionTokenTable(tag))

}

@Singleton
class SessionTokens @Inject()(
                               protected val databaseConfigProvider: DatabaseConfigProvider
                             )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[SessionTokens.SessionTokenTable, SessionToken, String](
    databaseConfigProvider,
    SessionTokens.TableQuery,
    executionContext,
    SessionTokens.module,
    SessionTokens.logger
  ) {

  private val schedulerExecutionContext: ExecutionContext = actors.Service.actorSystem.dispatchers.lookup("akka.actor.scheduler-dispatcher")

  object Service {

    def refresh(id: String): Future[String] = {
      val sessionToken: String = utilities.IdGenerator.getRandomHexadecimal

      //      val upsertToken = upsert(SessionToken(id, utilities.Secrets.sha256HashString(sessionToken), DateTime.now(DateTimeZone.UTC).getMillis))
      for {
        _ <- delete(id)
        _ <- create(SessionToken(id, utilities.Secrets.sha256HashString(sessionToken), DateTime.now(DateTimeZone.UTC).getMillis))
      } yield sessionToken
    }

    def tryGet(id: String): Future[SessionToken] = tryGetById(id)

    def tryVerifyingSessionToken(id: String, sessionToken: String): Future[Boolean] = {
      tryGetById(id).map { token =>
        if (token.sessionTokenHash == utilities.Secrets.sha256HashString(sessionToken)) true else false
      }
    }

    def tryVerifyingSessionTokenTime(id: String): Future[Boolean] = {
      tryGetById(id).map { token =>
        if (DateTime.now(DateTimeZone.UTC).getMillis - token.sessionTokenTime < constants.CommonConfig.sessionTokenTimeout) true else false
      }
    }

    def getTimedOutIDs: Future[Seq[String]] = filter(_.sessionTokenTime < DateTime.now(DateTimeZone.UTC).getMillis - constants.CommonConfig.sessionTokenTimeout).map(_.map(_.accountId))

    def deleteById(id: String): Future[Unit] = delete(id)

    def deleteSessionTokens(ids: Seq[String]): Future[Unit] = deleteMultiple(ids)

  }

  private val runnable = new Runnable {
    def run(): Unit = {
      val ids = Service.getTimedOutIDs

      def deleteSessionTokens(ids: Seq[String]) = Service.deleteSessionTokens(ids)

      val forComplete = (for {
        ids <- ids
        _ <- deleteSessionTokens(ids)
      } yield ()).recover {
        case baseException: BaseException => logger.error(baseException.failure.message)
      }
      Await.result(forComplete, Duration.Inf)
    }
  }

  actors.Service.actorSystem.scheduler.scheduleWithFixedDelay(initialDelay = 300.second, delay = 300.second)(runnable)(schedulerExecutionContext)
}