package constants

import akka.actor.Cancellable
import play.api.Logger

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, DurationInt, FiniteDuration, SECONDS}

abstract class Scheduler extends Runnable {

  val name: String
  val initalDelay: FiniteDuration = constants.Scheduler.InitialDelay
  val fixedDelay: FiniteDuration = constants.Scheduler.FixedDelay

  def runner(): Unit

  final def run(): Unit = if (!utilities.Scheduler.getSignalReceived) this.runner
  else utilities.Scheduler.shutdownThread(this.name)

  def start()(implicit schedulerExecutionContext: ExecutionContext, logger: Logger): Cancellable = {
    logger.info("Starting thread: " + this.name)
    actors.Service.actorSystem.scheduler.scheduleWithFixedDelay(initialDelay = initalDelay, delay = fixedDelay)(this)(schedulerExecutionContext)
  }
}

object Scheduler {
  val InitialDelay: FiniteDuration = constants.CommonConfig.initialDelay.millis
  val FixedDelay: FiniteDuration = constants.CommonConfig.fixedDelay.millis
  val HalfDay: FiniteDuration = Duration.create(43200, SECONDS)

  val BLOCKCHAIN_TRANSACTION_SEND_COIN = "BLOCKCHAIN_TRANSACTION_SEND_COIN"
  val BLOCKCHAIN_TRANSACTION_NFT_SALE = "BLOCKCHAIN_TRANSACTION_NFT_SALE"
  val BLOCKCHAIN_TRANSACTION_NFT_PUBLIC_LISTING = "BLOCKCHAIN_TRANSACTION_NFT_PUBLIC_LISTING"

  val MASTER_TRANSACTION_NFT_PUBLIC_LISTING = "MASTER_TRANSACTION_NFT_PUBLIC_LISTING"
  val MASTER_TRANSACTION_NFT_SALE = "MASTER_TRANSACTION_NFT_SALE"
  val MASTER_TRANSACTION_SESSION_TOKEN = "MASTER_TRANSACTION_SESSION_TOKEN"
  val MASTER_TRANSACTION_TOKEN_PRICE = "MASTER_TRANSACTION_TOKEN_PRICE"

  val HISTORY_MASTER_SALE = "HISTORY_MASTER_SALE"
  val HISTORY_MASTER_PUBLIC_LISTING = "HISTORY_MASTER_PUBLIC_LISTING"
}
