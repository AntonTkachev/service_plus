package com.service.plus

import org.apache.http.client.HttpResponseException
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import spray.json.{DefaultJsonProtocol, RootJsonFormat, _}

case class RootInterface(base: String,
                         rates: Map[String, Double],
                         date: String
                        )

//fixme rename
trait WebServerUtils extends DefaultJsonProtocol {
  implicit val RootInterfaceFormat: RootJsonFormat[RootInterface] = jsonFormat3(RootInterface)

  private def getBodyFromResponse(URI: String): String = {
    val httpClient = HttpClients.createDefault
    val request = new HttpGet(URI)
    val response = httpClient.execute(request)

    val statusLine = response.getStatusLine
    val statusCode = statusLine.getStatusCode
    if (statusCode != 200) {
      httpClient.close()
      throw new HttpResponseException(statusCode, statusLine.getReasonPhrase);
    } else {
      val body = EntityUtils.toString(response.getEntity)
      httpClient.close()
      body
    }
  }

  def getCurrencyData(URI: String): RootInterface = getBodyFromResponse(URI).parseJson.convertTo[RootInterface]
}
