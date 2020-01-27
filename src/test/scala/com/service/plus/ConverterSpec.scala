package com.service.plus

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.MissingQueryParamRejection
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._

class ConverterSpec extends FlatSpec with Matchers with ScalatestRouteTest {

  private def supposedException(uri: String) =
    s"""Request to http://example.com$uri could not be processed correctly!"""

  it should "correct get request" in {
    val from = "USD"
    val to = "RUB"
    val number = 42
    Get(s"/convert?from=$from&to=$to&number=$number") ~> WebServer.route ~> check {
      responseAs[String] shouldEqual Converter.currency(from, to, number)
    }
  }

  it should "throw HttpResponseException because currency 'from' not exist" in {
    val uri = "/convert?from=&to=RUB&number=42"
    Get(uri) ~> WebServer.route ~> check {
      status shouldEqual StatusCodes.BadRequest
      responseAs[String] shouldEqual supposedException(uri)
    }
  }

  it should "throw Exception because currency 'from' is empty" in {
    val uri = "/convert?from=&to=RUB&number=42"
    Get(uri) ~> WebServer.route ~> check {
      status shouldEqual StatusCodes.BadRequest
      responseAs[String] shouldEqual supposedException(uri)
    }
  }

  it should "throw Exception because currency 'to' is empty" in {
    val uri = "/convert?from=USD&to=&number=42"
    Get(uri) ~> WebServer.route ~> check {
      status shouldEqual StatusCodes.BadRequest
      responseAs[String] shouldEqual supposedException(uri)
    }
  }

  it should "throw Exception because currency 'number' is empty" in {
    Get("/convert?from=USD&to=RUB&number=") ~> WebServer.route ~> check {
      rejection shouldEqual MissingQueryParamRejection("number")
    }
  }
}
