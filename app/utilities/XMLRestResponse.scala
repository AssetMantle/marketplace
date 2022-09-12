package utilities

import play.api.mvc.{Result, Results}

import scala.xml.Elem

object XMLRestResponse {

  //Failure
  val REQUEST_NOT_WELL_FORMED = new XmlResponse(400, "BAD_REQUEST", "Request is not well-formed and cannot be understood.")

  class XmlResponse(code: Int, status: String, message: String) {
    val response: Elem = <response>
      <code>
        {code}
      </code>
      <status>
        {status}
      </status>
      <message>
        {message}
      </message>
    </response>

    def result: Result = Results.Status(code)(response).as("application/xml")
  }

}
