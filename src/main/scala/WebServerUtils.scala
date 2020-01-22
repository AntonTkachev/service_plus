import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import spray.json.{DefaultJsonProtocol, RootJsonFormat, _}

case class Rates(GBP: Double,
                 HKD: Double,
                 IDR: Double,
                 ILS: Double,
                 DKK: Double,
                 INR: Double,
                 CHF: Double,
                 MXN: Double,
                 CZK: Double,
                 SGD: Double,
                 THB: Double,
                 HRK: Double,
                 MYR: Double,
                 NOK: Double,
                 CNY: Double,
                 BGN: Double,
                 PHP: Double,
                 SEK: Double,
                 PLN: Double,
                 ZAR: Double,
                 CAD: Double,
                 ISK: Double,
                 BRL: Double,
                 RON: Double,
                 NZD: Double,
                 TRY: Double,
                 JPY: Double,
                 RUB: Double,
                 KRW: Double,
                 USD: Double,
                 HUF: Double,
                 AUD: Double
                )

case class RootInterface(base: String,
                         rates: Map[String, Double],
                         date: String
                        )


//fixme rename
trait WebServerUtils extends DefaultJsonProtocol {
  implicit val RootInterfaceFormat: RootJsonFormat[RootInterface] = jsonFormat3(RootInterface)
//  implicit val RatesFormat: RootJsonFormat[Rates] = jsonFormat3(Rates)

  def getBodyFromResponse(URI: String = Meta.URI): String = {
    val httpClient = HttpClients.createDefault
    val request = new HttpGet(URI)
    val response = httpClient.execute(request)
    httpClient.close()
    EntityUtils.toString(response.getEntity)
  }

  def getCurrencyData() = getBodyFromResponse().parseJson.convertTo[RootInterface]

  private object Meta {
    val URI = "https://api.ratesapi.io/api/latest"
  }

}