package com.service.plus

import org.apache.http.client.HttpResponseException
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

object Converter {
  def currency(from: String, to: String, number: Long): String = {
    require(from.nonEmpty, "From currency should be require")
    val json: String = getBodyFromResponse(s"https://api.ratesapi.io/api/latest?base=$from")
    val rootInterface = JsonParser.toRootInterface(json)
    val rates = rootInterface.rates
    rates.get(to) match {
      case Some(to) => s"""{"result":${number * to}}"""
      case _ => throw new IllegalArgumentException(s"Can't find data for currency $to")
    }
  }

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
}
