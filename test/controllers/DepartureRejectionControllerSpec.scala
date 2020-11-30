/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers

import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.FakeRequest
import play.api.test.Helpers._

class DepartureRejectionControllerSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite with OptionValues {

  private val departureId = 27
  private val guaranteeInValidMessageId = 2
  private val declarationRejectId = 33
  private val declarationRejectMessageId = 2
  private val cancellationDecisionUpdateId: Int = 39
  private val cancellationDecisionUpdateMessageId: Int = 2
  private val declarationCancellationId: Int = 45
  private val declarationCancellationMessageId: Int = 2
  private val invalidDepartureId = 11111

  "DepartureRejectionControllerSpec" - {

    "GET Summary" - {

      "must return departure summary" in {

        val request = FakeRequest(GET, routes.DepartureRejectionController.getSummary(departureId).url)
        val result = route(app, request).value

        status(result) mustEqual OK
        contentType(result).get mustEqual "application/json"
      }

      "must return departure declaration rejection summary" in {

        val request = FakeRequest(GET, routes.DepartureRejectionController.getSummary(declarationRejectId).url)
        val result = route(app, request).value

        status(result) mustEqual OK
        contentType(result).get mustEqual "application/json"
      }

      "must return cancellation decision update summary" in {

        val request = FakeRequest(GET, routes.DepartureRejectionController.getSummary(cancellationDecisionUpdateId).url)
        val result = route(app, request).value

        status(result) mustEqual OK
        contentType(result).get mustEqual "application/json"
      }

      "must return declaration cancellation summary" in {

        val request = FakeRequest(GET, routes.DepartureRejectionController.getSummary(declarationCancellationId).url)
        val result = route(app, request).value

        status(result) mustEqual OK
        contentType(result).get mustEqual "application/json"
      }

      "must return BadRequest for invalid input" in {

        val request = FakeRequest(GET, routes.DepartureRejectionController.getSummary(invalidDepartureId).url)
        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST
      }

    }

    "GetMessage" - {

      "must return rejected guarantee not valid message" in {

        val request = FakeRequest(GET, routes.DepartureRejectionController.getMessage(departureId, guaranteeInValidMessageId).url)
        val result = route(app, request).value

        status(result) mustEqual OK
        contentType(result).get mustEqual "application/json"
      }

      "must return departure rejection message" in {

        val request = FakeRequest(GET, routes.DepartureRejectionController.getMessage(declarationRejectId, declarationRejectMessageId).url)
        val result = route(app, request).value

        status(result) mustEqual OK
        contentType(result).get mustEqual "application/json"
      }

      "must return cancellation decision update message" in {

        val request = FakeRequest(GET, routes.DepartureRejectionController.getMessage(cancellationDecisionUpdateId, cancellationDecisionUpdateMessageId).url)
        val result = route(app, request).value

        status(result) mustEqual OK
        contentType(result).get mustEqual "application/json"
      }

      "must return declaration cancellation message" in {

        val request = FakeRequest(GET, routes.DepartureRejectionController.getMessage(declarationCancellationId, declarationCancellationMessageId).url)
        val result = route(app, request).value

        status(result) mustEqual OK
        contentType(result).get mustEqual "application/json"
      }

      "must return BadRequest for invalid inputs" in {

        val request = FakeRequest(GET, routes.DepartureRejectionController.getMessage(invalidDepartureId, guaranteeInValidMessageId).url)
        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST
      }

    }

  }

}
