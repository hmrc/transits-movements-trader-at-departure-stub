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

package config

import io.lemonlabs.uri.AbsoluteUrl
import io.lemonlabs.uri.UrlPath
import play.api.ConfigLoader
import play.api.Configuration

final case class Service(host: String, port: String, protocol: String, startUrl: String) {

  def baseUrl: AbsoluteUrl =
    AbsoluteUrl.parse(s"$protocol://$host:$port").withPath(UrlPath.parse(startUrl))
}

object Service {

  implicit lazy val configLoader: ConfigLoader[Service] = ConfigLoader {
    config => prefix =>
      val service  = Configuration(config).get[Configuration](prefix)
      val host     = service.get[String]("host")
      val port     = service.get[String]("port")
      val protocol = service.get[String]("protocol")
      val startUrl = service.get[String]("startUrl")

      Service(host, port, protocol, startUrl)
  }
}
