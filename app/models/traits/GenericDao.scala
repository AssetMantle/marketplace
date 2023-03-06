package models.traits

import play.api.db.slick._
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcProfile
import slick.lifted.{CanBeQueryCondition, Ordered}

import scala.concurrent.{ExecutionContext, Future}

trait GenericDao[T <: Table[E] with ModelTable[PK], E <: Entity[PK], PK] extends HasDatabaseConfigProvider[JdbcProfile] {

//  implicit val executionContext: ExecutionContext

  def count(): Future[Int]

  def getById(id: PK): Future[Option[E]]

  def getAll: Future[Seq[E]]

  def tryGetById(id: PK): Future[E]

  def filter[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]): Future[Seq[E]]

  def filterHead[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]): Future[E]

  def filterAndSortHead[C <: Rep[_]](expr: T => C)(sortExpr: T => C)(implicit wt: CanBeQueryCondition[C], ev: C => Ordered): Future[E]

  def create(entity: E): Future[Unit]

  def create(entities: Seq[E]): Future[Unit]

//  def upsert(entity: E): Future[Unit]

//  def upsert(entities: Seq[E]): Future[Unit]

  def update(update: E): Future[Unit]

  def delete(id: PK): Future[Unit]

  def deleteMultiple(ids: Seq[PK]): Future[Unit]

  def deleteAll(): Future[Unit]

  def exists(id: PK): Future[Boolean]
}
