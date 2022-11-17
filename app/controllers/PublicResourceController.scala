package controllers

import play.api.http.HttpErrorHandler
import play.api.mvc.{Action, AnyContent, ControllerHelpers}

import javax.inject.{Inject, Singleton}

@Singleton
class PublicResourceController @Inject()(
                                       errorHandler: HttpErrorHandler,
                                       meta: AssetsMetadata
                                     ) extends ControllerHelpers {

  private val assetsBuilder: AssetsBuilder = new AssetsBuilder(errorHandler, meta)

  def versioned(file: String, version: String): Action[AnyContent] = {
    assetsBuilder.at(path = "/public", file = file)
  }

}
