package models.blockchain

import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import schema.data.Data
import schema.id.base.DataID
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

// TODO rename dataBytes to dataProtoBytes
case class Meta(dataTypeID: String, dataHashID: Array[Byte], dataHashIDString: String, dataBytes: Array[Byte]) extends Entity2[String, Array[Byte]] {

  lazy val data: Data = Data(this.dataBytes)

  def id1: String = this.dataTypeID

  def id2: Array[Byte] = this.dataHashID

}

private[blockchain] object Metas {

  class MetaTable(tag: Tag) extends Table[Meta](tag, "Meta") with ModelTable2[String, Array[Byte]] {

    def * = (dataTypeID, dataHashID, dataHashIDString, dataBytes) <> (Meta.tupled, Meta.unapply)

    def dataTypeID = column[String]("dataTypeID", O.PrimaryKey)

    def dataHashID = column[Array[Byte]]("dataHashID", O.PrimaryKey)

    def dataHashIDString = column[String]("dataHashIDString")

    def dataBytes = column[Array[Byte]]("dataBytes")


    def id1 = dataTypeID

    def id2 = dataHashID

  }
}

@Singleton
class Metas @Inject()(
                       @NamedDatabase("explorer")
                       protected val dbConfigProvider: DatabaseConfigProvider,
                     )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl2[Metas.MetaTable, Meta, String, Array[Byte]]() {

  implicit val module: String = constants.Module.BLOCKCHAIN_META

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new Metas.MetaTable(tag))

  object Service {

    def get(id: DataID): Future[Option[Meta]] = getById(id1 = id.typeID.value, id2 = id.hashID.getBytes)

  }

}