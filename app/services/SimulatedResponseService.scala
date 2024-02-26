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

package services

import com.google.inject.ImplementedBy
import models.values.MessageSender
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpResponse

import javax.inject.Inject
import scala.concurrent.Future
import scala.xml.NodeSeq

@ImplementedBy(classOf[SimulatedResponseServiceImpl])
trait SimulatedResponseService {
  def simulateResponseTo(message: NodeSeq)(implicit hc: HeaderCarrier): Future[Option[HttpResponse]]
}

class SimulatedResponseServiceImpl @Inject()(guarantee: GuaranteeResponseService) extends SimulatedResponseService {

  def simulateResponseTo(message: NodeSeq)(implicit hc: HeaderCarrier): Future[Option[HttpResponse]] = {
    val messageSender = for {
      senderNode <- (message \\ "MesSenMES3").headOption
      senderText = senderNode.text
    } yield MessageSender(senderText)

    messageSender
      .map {
        case sender if sender.sendToGuarantee =>
          guarantee.simulateResponseTo(sender, message)
        case _ =>
          Future.successful(None)
      }
      .getOrElse {
        Future.successful(None)
      }
  }
}
