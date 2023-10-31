package models.blockchain

import models.Abstract.{Parameter => abstractParameter}
import models.common.Parameters._
import models.traits.Logging
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

case class Parameter(parameterType: String, value: abstractParameter, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging

@Singleton
class Parameters @Inject()(
                            @NamedDatabase("explorer")
                            protected val databaseConfigProvider: DatabaseConfigProvider,
                            utilitiesOperations: utilities.Operations,
                          )(implicit executionContext: ExecutionContext) {

  val databaseConfig = databaseConfigProvider.get[JdbcProfile]

  val db = databaseConfig.db

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_PARAMETER

  import databaseConfig.profile.api._

  val parameterTable = TableQuery[ParameterTable]

  case class ParameterSerialized(parameterType: String, value: String, createdBy: Option[String], createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) {
    def deserialize: Parameter = Parameter(parameterType = parameterType, value = utilities.JSON.convertJsonStringToObject[abstractParameter](value), createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)
  }

  def serialize(parameter: Parameter): ParameterSerialized = ParameterSerialized(parameterType = parameter.parameterType, value = Json.toJson(parameter.value).toString, createdBy = parameter.createdBy, createdOnMillisEpoch = parameter.createdOnMillisEpoch, updatedBy = parameter.updatedBy, updatedOnMillisEpoch = parameter.updatedOnMillisEpoch)

  private def tryGetByType(parameterType: String): Future[ParameterSerialized] = db.run(parameterTable.filter(_.parameterType === parameterType).result.head.asTry).map {
    case Success(result) => result
    case Failure(exception) => exception match {
      case noSuchElementException: NoSuchElementException => constants.Response.PARAMETER_NOT_FOUND.throwBaseException(noSuchElementException)
    }
  }

  private def getAllParameters: Future[Seq[ParameterSerialized]] = db.run(parameterTable.result)

  class ParameterTable(tag: Tag) extends Table[ParameterSerialized](tag, "Parameter") {

    def * = (parameterType, value, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (ParameterSerialized.tupled, ParameterSerialized.unapply)

    def parameterType = column[String]("parameterType", O.PrimaryKey)

    def value = column[String]("value")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")
  }

  object Service {

    def tryGetAuthParameter: Future[AuthParameter] = tryGetByType(schema.constants.Parameter.Type.AUTH).map(_.deserialize).map(_.value.asAuthParameter)

    def tryGetBankParameter: Future[BankParameter] = tryGetByType(schema.constants.Parameter.Type.BANK).map(_.deserialize).map(_.value.asBankParameter)

    def tryGetDistributionParameter: Future[DistributionParameter] = tryGetByType(schema.constants.Parameter.Type.DISTRIBUTION).map(_.deserialize).map(_.value.asDistributionParameter)

    def tryGetGovernanceParameter: Future[GovernanceParameter] = tryGetByType(schema.constants.Parameter.Type.GOVERNANCE).map(_.deserialize).map(_.value.asGovernanceParameter)

    def tryGetMintingParameter: Future[MintingParameter] = tryGetByType(schema.constants.Parameter.Type.MINT).map(_.deserialize).map(_.value.asMintingParameter)

    def tryGetSlashingParameter: Future[SlashingParameter] = tryGetByType(schema.constants.Parameter.Type.SLASHING).map(_.deserialize).map(_.value.asSlashingParameter)

    def tryGetStakingParameter: Future[StakingParameter] = tryGetByType(schema.constants.Parameter.Type.STAKING).map(_.deserialize).map(_.value.asStakingParameter)

    def tryGetClassificationParameter: Future[ClassificationParameter] = tryGetByType(schema.constants.Parameter.Type.CLASSIFICATIONS).map(_.deserialize).map(_.value.asClassificationParameter)

    def tryGetIdentityParameter: Future[IdentityParameter] = tryGetByType(schema.constants.Parameter.Type.IDENTITIES).map(_.deserialize).map(_.value.asIdentityParameter)

    def tryGetOrderParameter: Future[OrderParameter] = tryGetByType(schema.constants.Parameter.Type.ORDERS).map(_.deserialize).map(_.value.asOrderParameter)

    def tryGetSplitParameter: Future[SplitParameter] = tryGetByType(schema.constants.Parameter.Type.SPLITS).map(_.deserialize).map(_.value.asSplitParameter)

    def getAll: Future[Seq[Parameter]] = getAllParameters.map(_.map(_.deserialize))

  }
}