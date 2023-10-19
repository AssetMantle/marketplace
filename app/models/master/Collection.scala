package models.master

import models.common.Collection._
import models.master.Collections.CollectionTable
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import schema.data.base.NumberData
import schema.id.base.{ClassificationID, IdentityID}
import schema.property.base.{MesaProperty, MetaProperty}
import schema.qualified.{Immutables, Mutables}
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Collection(id: String, creatorId: String, classificationId: Option[String], name: String, description: String, socialProfiles: Seq[SocialProfile], nsfw: Boolean, properties: Option[Seq[Property]], profileFileName: Option[String], coverFileName: Option[String], public: Boolean, royalty: BigDecimal, isDefined: Option[Boolean], defineAsset: Boolean, rank: Int, onSecondaryMarket: Boolean, showAll: Boolean, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def getRoyaltyFee: BigDecimal = this.royalty

  def getCreatorIdentityID: IdentityID = utilities.Identity.getMantlePlaceIdentityID(this.creatorId)

  def getTotalWeight: Long = (this.properties.get.map(_.toMetaProperty) ++ utilities.Properties.getCollectionDefaultImmutableMetaProperties(collectionName = this.name, creatorID = this.creatorId)).map(_.getBondedWeight).sum

  def getImmutableMetaProperties: Seq[MetaProperty] = this.properties.get.filter(x => x.meta && !x.mutable).map(_.toMetaProperty) ++ utilities.Properties.getCollectionDefaultImmutableMetaProperties(collectionName = this.name, creatorID = this.creatorId)

  def getImmutableProperties: Seq[MesaProperty] = this.properties.get.filter(x => !x.meta && !x.mutable).map(_.toMesaProperty)

  def getMutableMetaProperties: Seq[MetaProperty] = this.properties.get.filter(x => x.meta && x.mutable).map(_.toMetaProperty)

  def getMutableProperties: Seq[MesaProperty] = this.properties.get.filter(x => !x.meta && x.mutable).map(_.toMesaProperty)

  def getImmutables: Immutables = Immutables(this.getImmutableMetaProperties ++ this.getImmutableProperties)

  def getMutables: Mutables = Mutables(this.getMutableMetaProperties ++ this.getMutableProperties)

  def getClassificationID: ClassificationID = utilities.Collection.getClassificationID(immutables = this.getImmutables, mutables = this.getMutables.add(Seq(schema.constants.Properties.BondAmountProperty.copy(data = NumberData(this.getBondAmount)))))

  def serialize(): Collections.CollectionSerialized = Collections.CollectionSerialized(
    id = this.id,
    creatorId = this.creatorId,
    classificationId = this.classificationId,
    name = this.name,
    description = this.description,
    socialProfiles = Json.toJson(this.socialProfiles).toString(),
    nsfw = this.nsfw,
    properties = this.properties.map(Json.toJson(_).toString()),
    profileFileName = this.profileFileName,
    coverFileName = this.coverFileName,
    public = this.public,
    royalty = this.royalty,
    isDefined = this.isDefined,
    defineAsset = this.defineAsset,
    rank = this.rank,
    onSecondaryMarket = this.onSecondaryMarket,
    showAll = this.showAll,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)

  def getWebsite: Option[String] = this.socialProfiles.find(_.name == constants.Collection.SocialProfile.WEBSITE).map(_.url)

  def getTwitter: Option[String] = this.socialProfiles.find(_.name == constants.Collection.SocialProfile.TWITTER).map("https://www.twitter.com/" + _.url)

  def getTwitterUsername: Option[String] = this.socialProfiles.find(_.name == constants.Collection.SocialProfile.TWITTER).map(_.url)

  def getInstagram: Option[String] = this.socialProfiles.find(_.name == constants.Collection.SocialProfile.INSTAGRAM).map("https://www.instagram.com/" + _.url)

  def getProfileFileURL: Option[String] = this.profileFileName.map(x => constants.CommonConfig.AmazonS3.s3BucketURL + utilities.Collection.getOthersFileAwsKey(collectionId = this.id, fileName = x))

  def getCoverFileURL: Option[String] = this.coverFileName.map(x => constants.CommonConfig.AmazonS3.s3BucketURL + utilities.Collection.getOthersFileAwsKey(collectionId = this.id, fileName = x))

  def isFractionalized: Boolean = this.getSupply.isDefined

  def getSupply: Option[Property] = this.properties.fold[Option[Property]](None)(_.find(_.name == schema.constants.Properties.SupplyProperty.id.keyID.value))

  def isLockable: Boolean = this.getLockable.isDefined

  def getLockable: Option[Property] = this.properties.fold[Option[Property]](None)(_.find(_.name == schema.constants.Properties.LockHeightProperty.id.keyID.value))

  def customBurnEnabled: Boolean = this.getBurnHeight.isDefined

  def getBurnHeight: Option[Property] = this.properties.fold[Option[Property]](None)(_.find(_.name == schema.constants.Properties.BurnHeightProperty.id.keyID.value))

  def customBondAmountEnabled: Boolean = this.getBondAmountProperty.isDefined

  def getBondAmountProperty: Option[Property] = this.properties.fold[Option[Property]](None)(_.find(_.name == schema.constants.Properties.BondAmountProperty.id.keyID.value))

  def getBondAmount: Long = if (this.customBondAmountEnabled) this.getBondAmountProperty.fold(0L)(x => if (x.defaultValue != "") x.defaultValue.toLong else 0L)
  else this.getTotalWeight * constants.Blockchain.BondRate
}

private[master] object Collections {

  case class CollectionSerialized(id: String, creatorId: String, classificationId: Option[String], name: String, description: String, socialProfiles: String, nsfw: Boolean, properties: Option[String], profileFileName: Option[String], coverFileName: Option[String], public: Boolean, royalty: BigDecimal, isDefined: Option[Boolean], defineAsset: Boolean, rank: Int, onSecondaryMarket: Boolean, showAll: Boolean, createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity[String] {
    def deserialize()(implicit module: String, logger: Logger): Collection = Collection(id = id, creatorId = creatorId, classificationId = classificationId, name = name, description = description, socialProfiles = utilities.JSON.convertJsonStringToObject[Seq[SocialProfile]](socialProfiles), nsfw = nsfw, properties = properties.map(utilities.JSON.convertJsonStringToObject[Seq[Property]](_)), profileFileName = profileFileName, coverFileName = coverFileName, public = public, royalty = royalty, isDefined = isDefined, defineAsset = defineAsset, rank = rank, onSecondaryMarket = onSecondaryMarket, showAll = showAll, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)
  }

  class CollectionTable(tag: Tag) extends Table[CollectionSerialized](tag, "Collection") with ModelTable[String] {

    def * = (id, creatorId, classificationId.?, name, description, socialProfiles, nsfw, properties.?, profileFileName.?, coverFileName.?, public, royalty, isDefined.?, defineAsset, rank, onSecondaryMarket, showAll, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (CollectionSerialized.tupled, CollectionSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def creatorId = column[String]("creatorId")

    def classificationId = column[String]("classificationId")

    def name = column[String]("name")

    def description = column[String]("description")

    def socialProfiles = column[String]("socialProfiles")

    def nsfw = column[Boolean]("nsfw")

    def properties = column[String]("properties")

    def profileFileName = column[String]("profileFileName")

    def coverFileName = column[String]("coverFileName")

    def public = column[Boolean]("public")

    def royalty = column[BigDecimal]("royalty")

    def isDefined = column[Boolean]("isDefined")

    def defineAsset = column[Boolean]("defineAsset")

    def rank = column[Int]("rank")

    def onSecondaryMarket = column[Boolean]("onSecondaryMarket")

    def showAll = column[Boolean]("showAll")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")


  }
}

@Singleton
class Collections @Inject()(
                             protected val dbConfigProvider: DatabaseConfigProvider,
                           )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[Collections.CollectionTable, Collections.CollectionSerialized, String]() {

  implicit val module: String = constants.Module.MASTER_COLLECTION

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new CollectionTable(tag))

  object Service {

    def add(collection: Collection): Future[String] = create(collection.serialize()).map(_.id)

    def update(collection: Collection): Future[Unit] = updateById(collection.serialize())

    def fetchAll(): Future[Seq[Collection]] = getAll.map(_.map(_.deserialize))

    def fetchAllForDefiningAsset(): Future[Seq[Collection]] = filter(x => x.defineAsset && !x.isDefined).map(_.map(_.deserialize))

    def markAsDefined(ids: Seq[String]): Future[Int] = customUpdate(tableQuery.filter(_.id.inSet(ids)).map(_.isDefined).update(true))

    def markAsDefinePending(ids: Seq[String]): Future[Int] = customUpdate(tableQuery.filter(_.id.inSet(ids)).map(_.isDefined.?).update(None))

    def markAsNotDefined(ids: Seq[String]): Future[Int] = customUpdate(tableQuery.filter(_.id.inSet(ids)).map(_.isDefined).update(false))

    def markListedOnSecondaryMarket(id: String): Future[Int] = customUpdate(tableQuery.filter(_.id === id).map(_.onSecondaryMarket).update(true))

    def removeFromListedOnSecondaryMarket(id: String): Future[Int] = customUpdate(tableQuery.filter(_.id === id).map(_.onSecondaryMarket).update(false))

    def totalOnSecondaryMarket: Future[Int] = filterAndCount(_.onSecondaryMarket)

    def get(id: String): Future[Option[Collection]] = getById(id).map(_.map(_.deserialize))

    def tryGet(id: String): Future[Collection] = tryGetById(id).map(_.deserialize)

    def getPublicByPageNumber(pageNumber: Int): Future[Seq[Collection]] = filterAndSortWithPagination(_.public)(_.createdOnMillisEpoch)(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage).map(_.map(_.deserialize))

    def getCollectionsByPage(collectionIds: Seq[String], pageNumber: Int): Future[Seq[Collection]] = filterAndSortWithPagination(_.id.inSet(collectionIds))(_.createdOnMillisEpoch)(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage).map(_.map(_.deserialize))

    def getCollections(collectionIds: Seq[String]): Future[Seq[Collection]] = filter(_.id.inSet(collectionIds)).map(_.map(_.deserialize))

    def totalCreated(creatorId: String): Future[Int] = filterAndCount(_.creatorId === creatorId)

    def countPublicCollections: Future[Int] = filterAndCount(_.public)

    def getByCreatorAndPage(creatorId: String, pageNumber: Int): Future[Seq[Collection]] = filterAndSortWithPagination(_.creatorId === creatorId)(_.createdOnMillisEpoch)(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage).map(_.map(_.deserialize))

    def getByCreator(creatorId: String): Future[Seq[(String, String)]] = filterAndSort(_.creatorId === creatorId)(_.name).map(_.map(x => x.id -> x.name))

    def isOwner(id: String, accountId: String): Future[Boolean] = filterAndExists(x => x.id === id && x.creatorId === accountId)

    def updateProfile(id: String, fileName: String): Future[Unit] = {
      val collection = tryGet(id)
      for {
        collection <- collection
        _ <- updateById(collection.copy(profileFileName = Option(fileName)).serialize())
      } yield ()
    }

    def updateCover(id: String, fileName: String): Future[Unit] = {
      val collection = tryGet(id)
      for {
        collection <- collection
        _ <- updateById(collection.copy(coverFileName = Option(fileName)).serialize())
      } yield ()
    }

    def countCreated(accountId: String): Future[Int] = filterAndCount(_.creatorId === accountId)

    def getCollectionNames(ids: Seq[String]): Future[Map[String, String]] = filter(_.id.inSet(ids)).map(_.map(x => x.id -> x.name).toMap)

    def delete(ids: Seq[String]): Future[Int] = filterAndDelete(_.id.inSet(ids))

    def getAllPublic: Future[Seq[Collection]] = filter(_.public).map(_.map(_.deserialize))

    def getSecondaryMarketByPageNumber(pageNumber: Int): Future[Seq[Collection]] = filterAndSortWithPagination(_.onSecondaryMarket)(_.rank)(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage).map(_.map(_.deserialize))

    def getDefined: Future[Seq[String]] = filter(_.isDefined).map(_.map(_.id))

  }
}