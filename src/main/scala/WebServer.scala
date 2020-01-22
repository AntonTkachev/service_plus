import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object WebServer extends WebServerUtils {

  def convert(key: String, value: String, number: Long): String = {
    getCurrencyData()
    ""
  }

  val r = path("convert") {
    get {
      parameters('from.as[String], 'to.as[String], 'number.as[Long]) { (from, to, number) =>
        complete {
          convert(from, to, number)
        }
      }
    }
  }

  def main(args: Array[String]) {

    implicit val system: ActorSystem = ActorSystem("my-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher


    val bindingFuture = Http().bindAndHandle(r, "localhost", 9999)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
