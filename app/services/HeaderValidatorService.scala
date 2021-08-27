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

import com.google.inject.ImplementedBy
import com.google.inject.Singleton
import play.api.mvc.Headers
import play.api.Logging

@Singleton
class HeaderValidatorServiceImpl extends HeaderValidatorService with Logging {

  def validate(headers: Headers): Boolean = {

    logger.debug(headers.headers.toString())

    val customProcessHost = headers.hasHeader("CustomProcessHost")

    val xCorrelationId = headers.hasHeader("X-Correlation-ID")

    val date = headers.hasHeader("Date")

    val contentType = headers.hasHeader("Content-Type")

    val accept = headers.get("Accept") match {
      case Some(h) => h.startsWith("application/xml")
      case None    => false
    }

    val messageType = headers.hasHeader("X-Message-Type")

    val messageSender = headers.hasHeader("X-Message-Sender")

    customProcessHost && xCorrelationId && date && contentType && accept && messageType && messageSender
  }

}

@ImplementedBy(classOf[HeaderValidatorServiceImpl])
trait HeaderValidatorService {
  def validate(headers: Headers): Boolean
}
