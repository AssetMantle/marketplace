package models.blockchain

import models.Abstract.PublicKey
import models.Trait.{Entity, GenericDaoImpl, Logged, ModelTable}
import models.common.Account.Vesting.VestingParameters
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.db.NamedDatabase
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

case class Account(address: String, username: String, accountType: Option[String], publicKey: Option[PublicKey], accountNumber: Int, sequence: Int, vestingParameters: Option[VestingParameters], createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged
