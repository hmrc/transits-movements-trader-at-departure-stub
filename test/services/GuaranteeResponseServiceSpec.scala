/*
 * Copyright 2021 HM Revenue & Customs
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

package services

import connectors.FakeGuaranteeTestSupportConnector
import models.BalanceRequestFunctionalError
import models.BalanceRequestSuccess
import models.BalanceRequestXmlError
import models.SimulatedGuaranteeResponse
import models.errors.FunctionalError
import models.values.{CurrencyCode, ErrorType, GuaranteeReference, MessageSender, TaxIdentifier, UniqueReference}
import org.scalacheck.Gen
import org.scalatest.Inside
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpResponse

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

class GuaranteeResponseServiceSpec extends AnyFreeSpec with Matchers with OptionValues with ScalaFutures with Inside with ScalaCheckPropertyChecks {

  def CD034A(accessCode: String) =
    <CD034A>
      <SynIdeMES1>UNOC</SynIdeMES1>
      <SynVerNumMES2>3</SynVerNumMES2>
      <MesSenMES3>MDTP-GUA-{UUID.randomUUID().toString.replaceAll("-", "").take(24)}</MesSenMES3>
      <MesRecMES6>NTA.GB</MesRecMES6>
      <DatOfPreMES9>{DateTimeFormatter.ofPattern("yyyyMMdd").format(OffsetDateTime.now(ZoneOffset.UTC))}</DatOfPreMES9>
      <TimOfPreMES10>{DateTimeFormatter.ofPattern("HHmm").format(OffsetDateTime.now(ZoneOffset.UTC))}</TimOfPreMES10>
      <IntConRefMES11>7acb933dbe7039</IntConRefMES11>
      <MesIdeMES19>7acb933dbe7039</MesIdeMES19>
      <MesTypMES20>GB034A</MesTypMES20>
      <TRAPRIRC1>
        <TINRC159>GB12345678900</TINRC159>
      </TRAPRIRC1>
      <GUAREF2>
        <GuaRefNumGRNREF21>05DE3300BE0001067A001017</GuaRefNumGRNREF21>
        <GUAQUE>
          <QueIdeQUE1>2</QueIdeQUE1>
        </GUAQUE>
        <ACCDOC728>
          <AccCodCOD729>{accessCode}</AccCodCOD729>
        </ACCDOC728>
      </GUAREF2>
    </CD034A>

  implicit val hc: HeaderCarrier = HeaderCarrier()

  def service(response: HttpResponse = HttpResponse(OK, "")) =
    new GuaranteeResponseService(FakeGuaranteeTestSupportConnector(response))

  private val NotMatchedErrorType: ErrorType               = ErrorType(12)
  private val UnsupportedGuaranteeTypeErrorType: ErrorType = ErrorType(14)

  val accessCodeGen = Gen
    .stringOfN(4, Gen.alphaNumChar)
    .suchThat {
      str =>
        !Set("906", "913", "914", "915", "916", "917", "918", "919", "000").contains(str.takeRight(3))
    }

  "GuaranteeResponseService.buildSimulatedResponseFor" - {
    "when given access code ending with 906" - {
      "should trigger functional error response  with the Error Type of 12" in {
        val simulatedResponse = service().buildSimulatedResponseFor(CD034A("E906")).value

        inside(simulatedResponse) {
          case SimulatedGuaranteeResponse(taxId, grn, origRef, response) =>
            taxId mustBe TaxIdentifier("GB12345678900")
            grn mustBe GuaranteeReference("05DE3300BE0001067A001017")
            origRef mustBe UniqueReference("7acb933dbe7039")
            response mustBe a[BalanceRequestFunctionalError]
            val errors = response.asInstanceOf[BalanceRequestFunctionalError].errors.toList
            val error  = FunctionalError(NotMatchedErrorType, "Foo.Bar(1).Baz", None)
            errors.contains(error) mustBe true
        }
      }
    }

    "when given access code ending with 913" - {
      "should trigger functional error response with the Error Type of 12 incorrect EORI" in {
        val simulatedResponse = service().buildSimulatedResponseFor(CD034A("E913")).value

        inside(simulatedResponse) {
          case SimulatedGuaranteeResponse(taxId, grn, origRef, response) =>
            taxId mustBe TaxIdentifier("GB12345678900")
            grn mustBe GuaranteeReference("05DE3300BE0001067A001017")
            origRef mustBe UniqueReference("7acb933dbe7039")
            response mustBe a[BalanceRequestFunctionalError]
            val errors = response.asInstanceOf[BalanceRequestFunctionalError].errors.toList
            val error  = FunctionalError(NotMatchedErrorType, "RC1.TIN", None)
            errors.contains(error) mustBe true
        }
      }
    }

    "when given access code ending with 914" - {
      "should trigger functional error response with the Error Type of 14" in {
        val simulatedResponse = service().buildSimulatedResponseFor(CD034A("E914")).value

        inside(simulatedResponse) {
          case SimulatedGuaranteeResponse(taxId, grn, origRef, response) =>
            taxId mustBe TaxIdentifier("GB12345678900")
            grn mustBe GuaranteeReference("05DE3300BE0001067A001017")
            origRef mustBe UniqueReference("7acb933dbe7039")
            response mustBe a[BalanceRequestFunctionalError]
            val errors = response.asInstanceOf[BalanceRequestFunctionalError].errors.toList
            val error  = FunctionalError(UnsupportedGuaranteeTypeErrorType, "GRR(1).GQY(1).Query identifier", Some("R261"))
            errors.contains(error) mustBe true
        }
      }
    }

    "when given access code ending with 915" - {
      "should trigger functional error response with the Error Type of 12 incorrect access code" in {
        val simulatedResponse = service().buildSimulatedResponseFor(CD034A("E915")).value

        inside(simulatedResponse) {
          case SimulatedGuaranteeResponse(taxId, grn, origRef, response) =>
            taxId mustBe TaxIdentifier("GB12345678900")
            grn mustBe GuaranteeReference("05DE3300BE0001067A001017")
            origRef mustBe UniqueReference("7acb933dbe7039")
            response mustBe a[BalanceRequestFunctionalError]
            val errors = response.asInstanceOf[BalanceRequestFunctionalError].errors.toList
            val error  = FunctionalError(NotMatchedErrorType, "GRR(1).ACC(1).Access code", None)
            errors.contains(error) mustBe true
        }
      }
    }

    "when given access code ending with 916" - {
      "should trigger functional error response with the Error Type of 12 incorrect guarantee reference number" in {
        val simulatedResponse = service().buildSimulatedResponseFor(CD034A("E916")).value

        inside(simulatedResponse) {
          case SimulatedGuaranteeResponse(taxId, grn, origRef, response) =>
            taxId mustBe TaxIdentifier("GB12345678900")
            grn mustBe GuaranteeReference("05DE3300BE0001067A001017")
            origRef mustBe UniqueReference("7acb933dbe7039")
            response mustBe a[BalanceRequestFunctionalError]
            val errors = response.asInstanceOf[BalanceRequestFunctionalError].errors.toList
            val error  = FunctionalError(NotMatchedErrorType, "GRR(1).Guarantee reference number (GRN)", None)
            errors.contains(error) mustBe true
        }
      }
    }

    "when given access code ending with 917" - {
      "should trigger functional error response" in {
        val simulatedResponse = service().buildSimulatedResponseFor(CD034A("E917")).value

        inside(simulatedResponse) {
          case SimulatedGuaranteeResponse(taxId, grn, origRef, response) =>
            taxId mustBe TaxIdentifier("GB12345678900")
            grn mustBe GuaranteeReference("05DE3300BE0001067A001017")
            origRef mustBe UniqueReference("7acb933dbe7039")
            response mustBe a[BalanceRequestXmlError]
        }
      }
    }

    "when given access code ending with 918" - {
      "should trigger functional error response with the Error Type of 12 guarantee reference number does not match Eori" in {
        val simulatedResponse = service().buildSimulatedResponseFor(CD034A("E918")).value

        inside(simulatedResponse) {
          case SimulatedGuaranteeResponse(taxId, grn, origRef, response) =>
            taxId mustBe TaxIdentifier("GB12345678900")
            grn mustBe GuaranteeReference("05DE3300BE0001067A001017")
            origRef mustBe UniqueReference("7acb933dbe7039")
            response mustBe a[BalanceRequestFunctionalError]
            val errors = response.asInstanceOf[BalanceRequestFunctionalError].errors.toList
            val error  = FunctionalError(NotMatchedErrorType, "GRR(1).OTG(1).TIN", None)
            errors.contains(error) mustBe true
        }
      }
    }

    "when given access code ending with 919" - {
      "should trigger functional error response with multiple match errors" in {
        val simulatedResponse = service().buildSimulatedResponseFor(CD034A("E919")).value

        inside(simulatedResponse) {
          case SimulatedGuaranteeResponse(taxId, grn, origRef, response) =>
            taxId mustBe TaxIdentifier("GB12345678900")
            grn mustBe GuaranteeReference("05DE3300BE0001067A001017")
            origRef mustBe UniqueReference("7acb933dbe7039")
            response mustBe a[BalanceRequestFunctionalError]
            val errors = response.asInstanceOf[BalanceRequestFunctionalError].errors.toList
            val error1 = FunctionalError(NotMatchedErrorType, "RC1.TIN", None)
            val error2 = FunctionalError(NotMatchedErrorType, "GRR(1).OTG(1).TIN", None)
            val error3 = FunctionalError(ErrorType(26), "RC1.TIN", None)
            errors mustEqual List(error1, error2, error3)
        }
      }
    }

    "when given access code ending with 000" - {
      "should not trigger any response" in {
        val simulatedResponse = service().buildSimulatedResponseFor(CD034A("T000"))
        simulatedResponse mustBe None
      }
    }

    "when given anything else" - {
      "should trigger success response" in forAll(accessCodeGen) {
        accessCode =>
          val simulatedResponse = service().buildSimulatedResponseFor(CD034A(accessCode)).value

          inside(simulatedResponse) {
            case SimulatedGuaranteeResponse(taxId, grn, origRef, response) =>
              taxId mustBe TaxIdentifier("GB12345678900")
              grn mustBe GuaranteeReference("05DE3300BE0001067A001017")
              origRef mustBe UniqueReference("7acb933dbe7039")

              response mustBe a[BalanceRequestSuccess]

              inside(response) {
                case BalanceRequestSuccess(balance, currency) =>
                  balance must be >= BigDecimal(0)
                  balance must be <= BigDecimal("9999999999999.99")
                  currency mustBe CurrencyCode("EUR")
              }
          }
      }
    }
  }

  "GuaranteeResponseService.simulateResponseTo" - {
    "when given an XML message to response to" - {
      "will return success response from connector" in {
        val sender   = MessageSender("MDTP-GUA-22b9899e24ee48e6a18997d1")
        val response = HttpResponse(CREATED, "")
        service(response).simulateResponseTo(sender, CD034A("A037")).futureValue mustBe Some(response)
      }

      "will return client error response from connector" in {
        val sender   = MessageSender("MDTP-GUA-22b9899e24ee48e6a18997d1")
        val response = HttpResponse(BAD_REQUEST, "Argh!!!")
        service(response).simulateResponseTo(sender, CD034A("E906")).futureValue mustBe Some(response)
      }

      "will return server error response from connector" in {
        val sender   = MessageSender("MDTP-GUA-22b9899e24ee48e6a18997d1")
        val response = HttpResponse(BAD_GATEWAY, "Kaboom!!!")
        service(response).simulateResponseTo(sender, CD034A("X917")).futureValue mustBe Some(response)
      }
    }
  }
}
