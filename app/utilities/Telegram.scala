package utilities

import org.apache.http.client.methods.{HttpGet, HttpPost}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import play.api.Logger
import play.api.libs.json.{Json, OWrites}

import scala.concurrent.{ExecutionContext, Future}

object Telegram {

  private val logger: Logger = Logger(this.getClass)

  private def canSend = constants.CommonConfig.Telegram.BotToken != "" && constants.CommonConfig.Telegram.ChatId != ""

  private val postURL = "https://api.telegram.org/bot" + constants.CommonConfig.Telegram.BotToken + "/sendMessage?chat_id=" + constants.CommonConfig.Telegram.ChatId

  private val getURL = "https://api.telegram.org/bot" + constants.CommonConfig.Telegram.BotToken + "/sendMessage?chat_id=" + constants.CommonConfig.Telegram.ChatId + "&text="

  case class Message(text: String)

  implicit val writes: OWrites[Message] = Json.writes[Message]

  private val httpClient = HttpClientBuilder.create()
    .setUserAgent("Telegram")
    .setMaxConnPerRoute(5)
    .build()

  private def doGet(message: String): Unit = try {
    val get = new HttpGet(getURL + message)
    get.setHeader("Content-type", "application/json")
    httpClient.execute(get)
  } catch {
    case exception: Exception => logger.error(exception.getLocalizedMessage)
  }

  private def doPost(data: String): Unit = try {
    val post = new HttpPost(postURL)
    post.setHeader("Content-type", "application/json")
    post.setEntity(new StringEntity(Json.toJson(Message(text = data)).toString()))
    httpClient.execute(post)
  } catch {
    case exception: Exception => logger.error(exception.getLocalizedMessage)
  }

  def send(message: String)(implicit executionContext: ExecutionContext): Future[Unit] = if (canSend) Future(doPost(message)) else Future()

}
