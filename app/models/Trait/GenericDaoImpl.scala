package models.Trait

import exceptions.BaseException
import org.postgresql.util.PSQLException
import play.api.Logger
import play.api.db.slick._
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcProfile
import slick.lifted.CanBeQueryCondition

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

abstract class GenericDaoImpl[T <: Table[E] with ModelTable[PK], E <: Entity[PK], PK: BaseColumnType](databaseConfigProvider: DatabaseConfigProvider, tableQuery: TableQuery[T], implicit val executionContext: ExecutionContext, implicit val module: String, implicit val logger: Logger) extends HasDatabaseConfigProvider[JdbcProfile] { //extends GenericDao[T, E, PK] {

  protected val dbConfigProvider: DatabaseConfigProvider = databaseConfigProvider

  def count(): Future[Int] = db.run(tableQuery.length.result)

  def getById(id: PK): Future[Option[E]] = db.run(tableQuery.filter(_.id === id).result.headOption)

  def tryGetById(id: PK): Future[E] = db.run(tableQuery.filter(_.id === id).result.head.asTry).map {
    case Success(result) => result
    case Failure(exception) => exception match {
      case noSuchElementException: NoSuchElementException => throw new BaseException(new constants.Response.Failure(module + "_NOT_FOUND"), noSuchElementException)
    }
  }

  def getAll: Future[Seq[E]] = db.run(tableQuery.result)

  def filter[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]): Future[Seq[E]] = db.run(tableQuery.filter(expr).result)

  def filterHead[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]): Future[E] = db.run(tableQuery.filter(expr).result.head.asTry).map {
    case Success(result) => result
    case Failure(exception) => exception match {
      case noSuchElementException: NoSuchElementException => throw new BaseException(new constants.Response.Failure(module + "_NOT_FOUND"), noSuchElementException)
    }
  }

  def create(entity: E): Future[Unit] = db.run((tableQuery += entity).asTry).map {
    case Success(result) => ()
    case Failure(exception) => exception match {
      case psqlException: PSQLException => throw new BaseException(new constants.Response.Failure(module + "_INSERT_FAILED"), psqlException)
    }
  }

  def create(entities: Seq[E]): Future[Unit] = db.run((tableQuery ++= entities).asTry).map {
    case Success(result) => ()
    case Failure(exception) => exception match {
      case psqlException: PSQLException => throw new BaseException(new constants.Response.Failure(module + "_INSERT_FAILED"), psqlException)
    }
  }

  def upsert(entity: E): Future[Unit] = db.run(tableQuery.insertOrUpdate(entity).asTry).map {
    case Success(result) => ()
    case Failure(exception) => exception match {
      case psqlException: PSQLException => throw new BaseException(new constants.Response.Failure(module + "_UPSERT_FAILED"), psqlException)
    }
  }

  def upsert(entities: Seq[E]): Future[Unit] = db.run(DBIO.sequence(entities.map(entity => tableQuery.insertOrUpdate(entity))).asTry).map {
    case Success(result) => ()
    case Failure(exception) => exception match {
      case psqlException: PSQLException => throw new BaseException(new constants.Response.Failure(module + "_UPSERT_FAILED"), psqlException)
    }
  }

  def update(update: E): Future[Unit] = db.run(tableQuery.filter(_.id === update.id).update(update).asTry).map {
    case Success(result) => ()
    case Failure(exception) => exception match {
      case psqlException: PSQLException => throw new BaseException(new constants.Response.Failure(module + "_UPDATE_FAILED"), psqlException)
      case noSuchElementException: NoSuchElementException => throw new BaseException(new constants.Response.Failure(module + "_UPDATE_FAILED"), noSuchElementException)
    }
  }

  def delete(id: PK): Future[Unit] = db.run(tableQuery.filter(_.id === id).delete.asTry).map {
    case Success(result) => ()
    case Failure(exception) => exception match {
      case psqlException: PSQLException => throw new BaseException(new constants.Response.Failure(module + "_DELETE_FAILED"), psqlException)
      case noSuchElementException: NoSuchElementException => throw new BaseException(new constants.Response.Failure(module + "_DELETE_FAILED"), noSuchElementException)
    }
  }

  def deleteMultiple(ids: Seq[PK]): Future[Unit] = db.run(tableQuery.filter(_.id.inSet(ids)).delete.asTry).map {
    case Success(result) => ()
    case Failure(exception) => exception match {
      case psqlException: PSQLException => throw new BaseException(new constants.Response.Failure(module + "_DELETE_FAILED"), psqlException)
      case noSuchElementException: NoSuchElementException => throw new BaseException(new constants.Response.Failure(module + "_DELETE_FAILED"), noSuchElementException)
    }
  }

  def deleteAll(): Future[Unit] = db.run((sqlu"""TRUNCATE TABLE ${tableQuery.baseTableRow.tableName} RESTART IDENTITY CASCADE""").asTry).map {
    case Success(result) => ()
    case Failure(exception) => exception match {
      case psqlException: PSQLException => throw new BaseException(new constants.Response.Failure(module + "_DELETE_ALL_FAILED"), psqlException)
    }
  }

  def exists(id: PK): Future[Boolean] = db.run(tableQuery.filter(_.id === id).exists.result)

}