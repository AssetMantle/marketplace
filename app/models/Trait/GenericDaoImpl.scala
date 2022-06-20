package models.Trait

import play.api.db.slick._
import slick.jdbc.H2Profile.api._
import slick.lifted.CanBeQueryCondition

import scala.concurrent.{ExecutionContext, Future}

abstract class GenericDaoImpl[T <: Table[E] with IdentifyableTable[PK], E <: Entity[PK], PK: BaseColumnType](databaseConfigProvider: DatabaseConfigProvider, tableQuery: TableQuery[T], implicit val executionContext: ExecutionContext) extends GenericDao[T, E, PK] { // extends HasDatabaseConfigProvider[JdbcProfile] { //

  protected val dbConfigProvider: DatabaseConfigProvider = databaseConfigProvider

  def count(): Future[Int] = db.run(tableQuery.length.result)

  def findById(id: PK): Future[Option[E]] = db.run(tableQuery.filter(_.id === id).result.headOption)

  def findAll(): Future[Seq[E]] = db.run(tableQuery.result)

  def filter[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]): Future[Seq[E]] = db.run(tableQuery.filter(expr).result)

  def create(entity: E): Future[Unit] = db.run(tableQuery += entity).map(_ => ())

  def create(entities: Seq[E]): Future[Unit] = db.run(tableQuery ++= entities).map(_ => ())

  def upsert(entity: E): Future[Unit] = db.run(tableQuery.insertOrUpdate(entity)).map(_ => ())

  def upsert(entities: Seq[E]): Future[Unit] = db.run(DBIO.sequence(entities.map(entity => tableQuery.insertOrUpdate(entity)))).map(_ => ())

  def update(update: E): Future[Unit] = db.run(tableQuery.filter(_.id === update.id).update(update)).map(_ => ())

  def delete(id: PK): Future[Unit] = db.run(tableQuery.filter(_.id === id).delete).map(_ => ())

  def deleteMultiple(ids: Seq[PK]): Future[Unit] = db.run(tableQuery.filter(_.id.inSet(ids)).delete).map(_ => ())

  def deleteAll(): Future[Unit] = db.run(sqlu"""TRUNCATE TABLE ${tableQuery.baseTableRow.tableName} RESTART IDENTITY CASCADE""").map(_ => ())
}