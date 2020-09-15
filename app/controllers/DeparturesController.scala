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

import com.google.inject.Inject
import config.AppConfig
import play.api.http.HeaderNames
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import utils.JsonUtils

class DeparturesController @Inject()(appConfig: AppConfig, cc: ControllerComponents, jsonUtils: JsonUtils)
    extends BackendController(cc) {

  def post: Action[AnyContent] = Action { implicit request =>
    request.headers.get(HeaderNames.AUTHORIZATION) match {
      case Some(value) =>
        if (value == s"Bearer ${appConfig.eisBearerToken}")
          Accepted
        else
          Unauthorized
      case None =>
        Unauthorized
    }
  }

  def getDepartures: Action[AnyContent] = Action {
    implicit request =>
      val json = jsonUtils.readJsonFromFile("conf/resources/departure-response.json")

      Ok(json).as("application/json")
  }
}
