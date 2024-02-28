/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import models.BalanceRequestSuccess
import models.SimulatedGuaranteeResponse
import models.values.CurrencyCode
import models.values.GuaranteeReference
import models.values.MessageSender
import models.values.TaxIdentifier
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import play.api.libs.json.Json
import models.values.UniqueReference

class GuaranteeTestSupportConnectorSpec
    extends AnyFreeSpec
    with Matchers
    with GuiceOneServerPerSuite
    with WireMockSpec
    with ScalaFutures
    with IntegrationPatience {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  override protected def portConfigKeys: Seq[String] =
    Seq("microservice.services.guarantee-test-support.port")

  "GuaranteeTestSupportConnector" - {
    "when given simulated response request" - {
      "should send request to the test support API" in {
        val connector     = app.injector.instanceOf[GuaranteeTestSupportConnector]
        val messageSender = MessageSender("MDTP-GUA-22b9899e24ee48e6a18997d1")

        wireMockServer.stubFor(
          post(urlPathMatching("/balances/MDTP-GUA-[a-f0-9]{24}"))
            .willReturn(aResponse().withStatus(OK))
            .withRequestBody(
              equalToJson(
                Json.stringify(
                  Json.obj(
                    "taxIdentifier"            -> "GB12345678900",
                    "guaranteeReference"       -> "05DE3300BE0001067A001017",
                    "originalMessageReference" -> "7acb933dbe7039",
                    "response" -> Json.obj(
                      "status"   -> "SUCCESS",
                      "balance"  -> 12345678.9,
                      "currency" -> "GBP"
                    )
                  )
                )
              )
            )
        )

        val simulatedResponse = SimulatedGuaranteeResponse(
          TaxIdentifier("GB12345678900"),
          GuaranteeReference("05DE3300BE0001067A001017"),
          UniqueReference("7acb933dbe7039"),
          BalanceRequestSuccess(BigDecimal("12345678.90"), CurrencyCode("GBP"))
        )

        val response = connector.simulateResponse(messageSender, simulatedResponse).futureValue

        response.status mustBe OK
      }

      "should pass back client error" in {
        val connector     = app.injector.instanceOf[GuaranteeTestSupportConnector]
        val messageSender = MessageSender("MDTP-GUA-22b9899e24ee48e6a18997d1")

        wireMockServer.stubFor(
          post(urlPathMatching("/balances/MDTP-GUA-[a-f0-9]{24}"))
            .willReturn(aResponse().withStatus(BAD_REQUEST))
        )

        val simulatedResponse = SimulatedGuaranteeResponse(
          TaxIdentifier("GB12345678900"),
          GuaranteeReference("05DE3300BE0001067A001017"),
          UniqueReference("7acb933dbe7039"),
          BalanceRequestSuccess(BigDecimal("12345678.90"), CurrencyCode("GBP"))
        )

        val response = connector.simulateResponse(messageSender, simulatedResponse).futureValue

        response.status mustBe BAD_REQUEST
      }

      "should pass back server error" in {
        val connector     = app.injector.instanceOf[GuaranteeTestSupportConnector]
        val messageSender = MessageSender("MDTP-GUA-22b9899e24ee48e6a18997d1")

        wireMockServer.stubFor(
          post(urlPathMatching("/balances/MDTP-GUA-[a-f0-9]{24}"))
            .willReturn(aResponse().withStatus(BAD_GATEWAY))
        )

        val simulatedResponse = SimulatedGuaranteeResponse(
          TaxIdentifier("GB12345678900"),
          GuaranteeReference("05DE3300BE0001067A001017"),
          UniqueReference("7acb933dbe7039"),
          BalanceRequestSuccess(BigDecimal("12345678.90"), CurrencyCode("GBP"))
        )

        val response = connector.simulateResponse(messageSender, simulatedResponse).futureValue

        response.status mustBe BAD_GATEWAY
      }
    }
  }
}
