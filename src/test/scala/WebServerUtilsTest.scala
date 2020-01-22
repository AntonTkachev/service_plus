import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._

class WebServerUtilsTest extends FlatSpec with Matchers with ScalatestRouteTest {

  it should "correct get request" in {
    Get("/convert?from=USD&to=RUB&number=42") ~> WebServer.r ~> check {
      responseAs[String] shouldEqual "The color is 'blue' and the background is 'red'"
    }
  }
}
