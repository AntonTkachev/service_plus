package com.service.plus

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, extractUri, get, handleExceptions, parameters, path, _}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.ActorMaterializer
import org.apache.http.client.HttpResponseException

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object WebServer extends WebServerUtils {

  def convert(from: String, to: String, number: Long): String = {
    require(from.nonEmpty, "From currency should be require")
    val rootInterface: RootInterface = getCurrencyData(s"https://api.ratesapi.io/api/latest?base=$from")
    val rates = rootInterface.rates
    rates.get(to) match {
      case Some(to) => s"""{"result":${number * to}}"""
      case _ => throw new IllegalArgumentException(s"Can't find data for currency $to")
    }
  }

  private def exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case _: HttpResponseException =>
        extractUri { uri =>
          complete(HttpResponse(StatusCodes.BadRequest, entity = s"Request to $uri could not be processed correctly!"))
        }
      case _: IllegalArgumentException =>
        extractUri { uri =>
          complete(HttpResponse(StatusCodes.BadRequest, entity = s"Request to $uri could not be processed correctly!"))
        }
    }

  private[plus] val route: Route = path("convert") {
    get {
      parameters('from.as[String], 'to.as[String], 'number.as[Long]) { (from, to, number) =>
        handleExceptions(exceptionHandler) {
          complete {
            convert(from, to, number)
          }
        }
      }
    }
  }

  def main(args: Array[String]) {
    implicit val system: ActorSystem = ActorSystem("currency_converter")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val host = "localhost"
    val port = 9999
    val bindingFuture = Http().bindAndHandle(route, host, port)

    println(s"Server online at http://$host:$port/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
