package utilities

import play.api.Configuration

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Operations  @Inject()()(implicit executionContext: ExecutionContext, configuration: Configuration) {

  def traverse[A, B](values: Seq[A])(func: A => Future[B]): Future[Seq[B]] = values.foldLeft(Future.successful(Seq.empty[B])) { (accum, host) =>
      for {
        a <- accum
        i <- func(host)
      } yield (a :+ i)
    }

}