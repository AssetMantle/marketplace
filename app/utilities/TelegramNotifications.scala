package utilities

import play.api.libs.ws.WSClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class TelegramNotifications @Inject()(
                                       wsClient: WSClient,
                                     )(implicit executionContext: ExecutionContext) {

  private val canSend = constants.CommonConfig.Telegram.BotToken != "" && constants.CommonConfig.Telegram.ChatId != ""

  private val url = "https://api.telegram.org/bot" + constants.CommonConfig.Telegram.BotToken + "/sendMessage?chat_id=" + constants.CommonConfig.Telegram.ChatId + "&text="

  def send(message: String): Unit = if (canSend && message != "") wsClient.url(url + message).get

}
