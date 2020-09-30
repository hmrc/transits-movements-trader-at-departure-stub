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

package services

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.mvc.Headers

class HeaderValidatorServiceSpec extends AnyFreeSpec with Matchers {

  private val headerValidatorServiceSpec = new HeaderValidatorServiceImpl()

  "HeaderValidatorServiceSpec" - {

    "validate should return true if valid headers are specified" in {

      val validHeaders: Seq[(String, String)] = Seq(
        "X-Forwarded-Host" -> "mdtp",
        "X-Correlation-ID" -> "137302f5-71ae-40a4-bd92-cac2ae7sde2f",
        "Date" -> "Tue, 29 Sep 2020 11:46:50 +0100",
        "Content-Type" -> "application/xml",
        "Accept" -> "application/xml",
        "X-Message-Type" -> "IE015",
        "X-Message-Sender" -> "MDTP-000000000000000000000000011-01"
      )

      val result = headerValidatorServiceSpec.validate(new Headers(validHeaders))

      result mustBe true
    }

    "validate should return false if invalid headers are specified" in {

      val invalidHeaders: Seq[(String, String)] = Seq(
        "X-Forwarded-Host" -> "mdtp",
        "X-Correlation-ID" -> "137302f5-71ae-40a4-bd92-cac2ae7sde2f",
        "Date" -> "Tue, 29 Sep 2020 11:46:50 +0100",
        "Content-Type" -> "application/xml",
        "Accept" -> "application/xml"
      )

      val result = headerValidatorServiceSpec.validate(new Headers(invalidHeaders))

      result mustBe false
    }
  }

}
