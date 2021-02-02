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

package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import utils.JsonUtils

class DepartureRejectionController @Inject()(cc: ControllerComponents, jsonUtils: JsonUtils) extends BackendController(cc) {

  private val GuaranteeNotValidDepartureId: Int = 27
  private val GuaranteeNotValidMessageId: Int = 2
  private val DeclarationRejectionDepartureId: Int = 33
  private val DeclarationRejectionMessageId: Int = 2
  private val CancellationDecisionUpdateId: Int = 39
  private val CancellationDecisionUpdateMessageId: Int = 2
  private val DeclarationCancellationId: Int = 45
  private val DeclarationCancellationMessageId: Int = 2
  private val NoReleaseForTransitId: Int = 31
  private val NoReleaseForTransitMessageId: Int = 2
  private val ControlDecisionId: Int = 32
  private val ControlDecisionMessageId: Int = 2

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
      case DeclarationCancellationId =>
        val json = jsonUtils.readJsonFromFile("conf/resources/departure-summary-user-submits-declaration-cancellation.json")
        Ok(json).as("application/json")
      case NoReleaseForTransitId =>
        val json = jsonUtils.readJsonFromFile("conf/resources/departure-summary-no-release-for-transit.json")
        Ok(json).as("application/json")
      case ControlDecisionId =>
        val json = jsonUtils.readJsonFromFile("conf/resources/departure-summary-control-decision.json")
        Ok(json).as("application/json")
      case _ => BadRequest
    }
  }

  def getMessage(arrivalId: Int, messageId: Int): Action[AnyContent] = Action {
    (arrivalId, messageId) match {
      case (GuaranteeNotValidDepartureId, GuaranteeNotValidMessageId)           => Ok(jsonUtils.readJsonFromFile("conf/resources/guarantee-not-valid-rejection.json")).as("application/json")
      case (DeclarationRejectionDepartureId, DeclarationRejectionMessageId)     => Ok(jsonUtils.readJsonFromFile("conf/resources/declaration-rejection.json")).as("application/json")
      case (CancellationDecisionUpdateId, CancellationDecisionUpdateMessageId)  => Ok(jsonUtils.readJsonFromFile("conf/resources/cancellation-decision-message-status-update.json")).as("application/json")
      case (DeclarationCancellationId, DeclarationCancellationMessageId)        => Ok(jsonUtils.readJsonFromFile("conf/resources/user-submits-declaration-cancellation.json")).as("application/json")
      case (NoReleaseForTransitId, NoReleaseForTransitMessageId)                => Ok(jsonUtils.readJsonFromFile("conf/resources/no-release-for-transit.json")).as("application/json")
      case (ControlDecisionId, ControlDecisionMessageId)                        => Ok(jsonUtils.readJsonFromFile("conf/resources/control-decision.json")).as("application/json")
      case _                                                                    => BadRequest
    }
  }


}
