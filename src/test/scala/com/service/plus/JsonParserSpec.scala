package com.service.plus

import org.scalatest.{FlatSpec, Matchers}
import spray.json.DeserializationException

class JsonParserSpec extends FlatSpec with Matchers {

  it should "throw DeserializationException because json is wrong" in {
    an[DeserializationException] shouldBe thrownBy(JsonParser.toRootInterface("""{"1":"1"}"""))
  }
}
