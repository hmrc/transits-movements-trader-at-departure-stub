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

package controllers

import com.google.inject.Inject
import controllers.DepartureRejectionController._
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.JsonUtils

class DepartureRejectionController @Inject()(cc: ControllerComponents, jsonUtils: JsonUtils) extends BackendController(cc) {

  def getSummary(departureId: Int): Action[AnyContent] = Action {
    departureId match {
      case GuaranteeNotValidDepartureId =>
        val json = jsonUtils.readJsonFromFile("conf/resources/departure-summary-guarantee-not-valid.json")
        Ok(json).as("application/json")
      case DeclarationRejectionDepartureId =>
        val json = jsonUtils.readJsonFromFile("conf/resources/departure-summary-declaration-rejection.json")
        Ok(json).as("application/json")
      case CancellationDecisionUpdateId =>
        val json = jsonUtils.readJsonFromFile("conf/resources/departure-summary-cancellation-decision-update.json")
        Ok(json).as("application/json")
      case CancellationDecisionUpdateRejectionId =>
        val json = jsonUtils.readJsonFromFile("conf/resources/departure-summary-cancellation-decision-update-rejection.json")
        Ok(json).as("application/json")
      case DeclarationCancellationId =>
        val json = jsonUtils.readJsonFromFile("conf/resources/departure-summary-user-submits-declaration-cancellation.json")
        Ok(json).as("application/json")
      case NoReleaseForTransitId =>
        val json = jsonUtils.readJsonFromFile("conf/resources/departure-summary-no-release-for-transit.json")
        Ok(json).as("application/json")
      case ControlDecisionId =>
        val json = jsonUtils.readJsonFromFile("conf/resources/departure-summary-control-decision.json")
        Ok(json).as("application/json")
      case CancellationRequestDepartureId =>
        val json = jsonUtils.readJsonFromFile("conf/resources/departure-summary-cancellation-request.json")
        Ok(json).as("application/json")
      case _ => BadRequest
    }
  }

  def getMessage(arrivalId: Int, messageId: Int): Action[AnyContent] = Action {
    (arrivalId, messageId) match {
      case (GuaranteeNotValidDepartureId, GuaranteeNotValidMessageId) =>
        Ok(jsonUtils.readJsonFromFile("conf/resources/guarantee-not-valid-rejection.json"))
          .as("application/json")
      case (DeclarationRejectionDepartureId, DeclarationRejectionMessageId) =>
        Ok(jsonUtils.readJsonFromFile("conf/resources/declaration-rejection.json")).as("application/json")
      case (CancellationDecisionUpdateId, CancellationDecisionUpdateMessageId) =>
        Ok(jsonUtils.readJsonFromFile("conf/resources/cancellation-decision-message-status-update.json"))
          .as("application/json")
      case (CancellationDecisionUpdateRejectionId, CancellationDecisionUpdateMessageRejectionId) =>
        Ok(jsonUtils.readJsonFromFile("conf/resources/cancellation-decision-message-status-update-rejection.json"))
          .as("application/json")
      case (DeclarationCancellationId, DeclarationCancellationMessageId) =>
        Ok(jsonUtils.readJsonFromFile("conf/resources/user-submits-declaration-cancellation.json"))
          .as("application/json")
      case (NoReleaseForTransitId, NoReleaseForTransitMessageId) =>
        Ok(jsonUtils.readJsonFromFile("conf/resources/no-release-for-transit.json")).as("application/json")
      case (ControlDecisionId, ControlDecisionMessageId) =>
        Ok(jsonUtils.readJsonFromFile("conf/resources/control-decision.json"))
          .as("application/json")
      case (CancellationRequestDepartureId, MRNAllocatedMessageId) =>
        Ok(jsonUtils.readJsonFromFile("conf/resources/mrn-allocated.json"))
          .as("application/json")
      case _ => BadRequest
    }
  }

  def postMessage(departureId: Int): Action[AnyContent] = Action {
    _ =>
      Accepted
  }

}

object DepartureRejectionController {
  val GuaranteeNotValidDepartureId: Int                 = 27
  val GuaranteeNotValidMessageId: Int                   = 2
  val DeclarationRejectionDepartureId: Int              = 33
  val DeclarationRejectionMessageId: Int                = 2
  val CancellationDecisionUpdateId: Int                 = 39
  val CancellationDecisionUpdateMessageId: Int          = 2
  val CancellationDecisionUpdateRejectionId: Int        = 88
  val CancellationDecisionUpdateMessageRejectionId: Int = 2
  val DeclarationCancellationId: Int                    = 45
  val DeclarationCancellationMessageId: Int             = 2
  val NoReleaseForTransitId: Int                        = 31
  val NoReleaseForTransitMessageId: Int                 = 2
  val ControlDecisionId: Int                            = 32
  val ControlDecisionMessageId: Int                     = 2
  val CancellationRequestDepartureId: Int               = 23
  val MRNAllocatedMessageId: Int                        = 2
  val departureId                                       = 27
  val guaranteeInValidMessageId                         = 2
  val declarationRejectId                               = 33
  val declarationRejectMessageId                        = 2
  val cancellationDecisionUpdateId: Int                 = 39
  val cancellationDecisionUpdateMessageId: Int          = 2
  val declarationCancellationId: Int                    = 45
  val declarationCancellationMessageId: Int             = 2
  val invalidDepartureId                                = 11111
  val controlDecisionId: Int                            = 32
  val controlDecisionMessageId: Int                     = 2
  val cancellationRequestDepartureId: Int               = 23
  val mRNAllocatedMessageId: Int                        = 2
}
