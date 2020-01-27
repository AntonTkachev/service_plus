package com.service.plus

import spray.json.{DefaultJsonProtocol, RootJsonFormat, _}

case class CurrencyRates(base: String,
                         rates: Map[String, Double],
                         date: String)

object JsonParser extends DefaultJsonProtocol {
  private implicit val RootInterfaceFormat: RootJsonFormat[CurrencyRates] = jsonFormat3(CurrencyRates)

  def toRootInterface(json: String): CurrencyRates = json.parseJson.convertTo[CurrencyRates]
}
