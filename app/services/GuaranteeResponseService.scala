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

import cats.data.NonEmptyList
import connectors.GuaranteeTestSupportConnector
import models.BalanceRequestFunctionalError
import models.BalanceRequestResponse
import models.BalanceRequestSuccess
import models.BalanceRequestXmlError
import models.SimulatedGuaranteeResponse
import models.errors.FunctionalError
import models.errors.XmlError
import models.values.CurrencyCode
import models.values.ErrorType
import models.values.GuaranteeReference
import models.values.MessageSender
import models.values.TaxIdentifier
import models.values.UniqueReference
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpResponse

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.math.BigDecimal.RoundingMode
import scala.util.Random
import scala.xml.NodeSeq

class GuaranteeResponseService @Inject()(connector: GuaranteeTestSupportConnector)(implicit ec: ExecutionContext) {
  private val NotMatchedErrorType: ErrorType               = ErrorType(12)
  private val UnsupportedGuaranteeTypeErrorType: ErrorType = ErrorType(14)

  private val FunctionalNack           = "906"
  private val UnsupportedGuaranteeType = "914"
  private val XmlNack                  = "917"
  private val Timeout                  = "000"

  // The schema mandates 16 chars maximum including the decimal point
  private val Decimal16 = 9999999999999.99

  /* For the purposes of this stub we will use the access code provided by the caller
   * to decide which simulated response to trigger
   */
  private def responseFromCode(responseCode: String): Option[BalanceRequestResponse] = responseCode match {
    case FunctionalNack =>
      val error = FunctionalError(NotMatchedErrorType, "Foo.Bar(1).Baz", None)
      Some(BalanceRequestFunctionalError(NonEmptyList.one(error)))
    case UnsupportedGuaranteeType =>
      val error = FunctionalError(UnsupportedGuaranteeTypeErrorType, "GRR(1).GQY(1).Query identifier", Some("R261"))
      Some(BalanceRequestFunctionalError(NonEmptyList.one(error)))
    case XmlNack =>
      val error = XmlError(ErrorType(14), "Foo.Bar(1).Baz", None)
      Some(BalanceRequestXmlError(NonEmptyList.one(error)))
    case Timeout =>
      None
    case _ =>
      val randomBalance = Random.nextDouble().abs * Decimal16
      val balance       = BigDecimal(randomBalance).setScale(2, RoundingMode.HALF_EVEN)
      val currency      = CurrencyCode("EUR")
      Some(BalanceRequestSuccess(balance, currency))
  }

  def buildSimulatedResponseFor(message: NodeSeq): Option[SimulatedGuaranteeResponse] =
    for {
      taxIdNode <- (message \\ "TINRC159").headOption
      taxIdentifier = taxIdNode.text
      guaranteeRefNode <- (message \\ "GuaRefNumGRNREF21").headOption
      guaranteeReference = guaranteeRefNode.text
      origRefNode <- (message \\ "MesIdeMES19").headOption
      origMessageReference = origRefNode.text
      accessCodeNode <- (message \\ "AccCodCOD729").headOption
      accessCode       = accessCodeNode.text
      accessCodeSuffix = accessCode.takeRight(3)
      response <- responseFromCode(accessCodeSuffix)
    } yield
      SimulatedGuaranteeResponse(
        TaxIdentifier(taxIdentifier),
        GuaranteeReference(guaranteeReference),
        UniqueReference(origMessageReference),
        response
      )

  def simulateResponseTo(messageSender: MessageSender, message: NodeSeq)(implicit hc: HeaderCarrier): Future[Option[HttpResponse]] =
    buildSimulatedResponseFor(message)
      .map {
        response =>
          connector.simulateResponse(messageSender, response).map(Some.apply)
      }
      .getOrElse {
        Future.successful(None)
      }
}
