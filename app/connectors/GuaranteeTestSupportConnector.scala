/*
 * Copyright 2022 HM Revenue & Customs
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

import com.google.inject.ImplementedBy
import config.AppConfig
import models.SimulatedGuaranteeResponse
import models.values.MessageSender
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.HttpResponse

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@ImplementedBy(classOf[GuaranteeTestSupportConnectorImpl])
trait GuaranteeTestSupportConnector {
  def simulateResponse(messageSender: MessageSender, simulatedResponse: SimulatedGuaranteeResponse)(implicit hc: HeaderCarrier): Future[HttpResponse]
}

@Singleton
class GuaranteeTestSupportConnectorImpl @Inject()(appConfig: AppConfig, http: HttpClient)(implicit ec: ExecutionContext) extends GuaranteeTestSupportConnector {

  def simulateResponse(messageSender: MessageSender, simulatedResponse: SimulatedGuaranteeResponse)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val url = appConfig.guaranteeTestSupportUrl.addPathPart(messageSender.value)
    http.POST[SimulatedGuaranteeResponse, HttpResponse](url.toString, simulatedResponse)
  }
}
