package utilities

import play.api.routing.JavaScriptReverseRoute

object JsRoutes {

  /**
   * @param route JavaScriptReverseRoute
   * @param parameters parameters that goes into the route. Should be given as s"'${some.value}', 94, '${name}'"
   * @return jsRoute object
   */
  def getJsRouteString(route: JavaScriptReverseRoute, parameters: String): String = if (route != null) {
    s"jsRoutes.${route.name}($parameters)"
  } else {
    "#"
  }

  def getJsRouteFunction(route: JavaScriptReverseRoute): String = if (route != null) {
    s"jsRoutes.${route.name}"
  } else {
    "#"
  }
}
