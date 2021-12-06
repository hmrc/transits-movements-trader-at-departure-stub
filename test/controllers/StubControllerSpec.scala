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

import akka.actor.ActorSystem
import akka.stream.Materializer
import config.AppConfig
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.Environment
import play.api.http.HeaderNames
import play.api.libs.json.Json
import play.api.mvc.Headers
import play.api.test.FakeHeaders
import play.api.test.FakeRequest
import play.api.test.Helpers
import play.api.test.Helpers._
import services.FakeSimulatedResponseService
import services.HeaderValidatorServiceImpl
import uk.gov.hmrc.http.HttpResponse
import utils.JsonUtils

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random
import scala.xml.Elem

class StubControllerSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite with OptionValues {

  implicit val system       = ActorSystem(suiteName)
  implicit val materializer = Materializer(system)

  private val env           = Environment.simple()
  private val configuration = Configuration.load(env)

  private val appConfig       = new AppConfig(configuration)
  private val jsonUtils       = new JsonUtils(env)
  private val headerValidator = new HeaderValidatorServiceImpl()

  private def controller(response: Option[HttpResponse] = None) =
    new StubController(appConfig, Helpers.stubControllerComponents(), headerValidator, FakeSimulatedResponseService(response), jsonUtils)

  val CC015B = <CC015B>
    <SynIdeMES1>UNOC</SynIdeMES1>
    <SynVerNumMES2>3</SynVerNumMES2>
    <MesRecMES6>NCTS</MesRecMES6>
    <DatOfPreMES9>20190912</DatOfPreMES9>
    <TimOfPreMES10>1222</TimOfPreMES10>
    <IntConRefMES11>WE190912102530</IntConRefMES11>
    <AppRefMES14>NCTS</AppRefMES14>
    <TesIndMES18>0</TesIndMES18>
    <MesIdeMES19>1</MesIdeMES19>
    <MesTypMES20>GB015B</MesTypMES20>
    <HEAHEA>
      <RefNumHEA4>01CTC201909121215</RefNumHEA4>
      <TypOfDecHEA24>T2</TypOfDecHEA24>
      <CouOfDesCodHEA30>IT</CouOfDesCodHEA30>
      <AgrLocOfGooCodHEA38>default</AgrLocOfGooCodHEA38>
      <AgrLocOfGooHEA39>default</AgrLocOfGooHEA39>
      <AgrLocOfGooHEA39LNG>EN</AgrLocOfGooHEA39LNG>
      <AutLocOfGooCodHEA41>default</AutLocOfGooCodHEA41>
      <PlaOfLoaCodHEA46>DOVER</PlaOfLoaCodHEA46>
      <CouOfDisCodHEA55>GB</CouOfDisCodHEA55>
      <CusSubPlaHEA66>default</CusSubPlaHEA66>
      <InlTraModHEA75>20</InlTraModHEA75>
      <IdeOfMeaOfTraAtDHEA78>EU_EXIT</IdeOfMeaOfTraAtDHEA78>
      <IdeOfMeaOfTraAtDHEA78LNG>EN</IdeOfMeaOfTraAtDHEA78LNG>
      <IdeOfMeaOfTraCroHEA85>EU_EXIT</IdeOfMeaOfTraCroHEA85>
      <IdeOfMeaOfTraCroHEA85LNG>EN</IdeOfMeaOfTraCroHEA85LNG>
      <ConIndHEA96>0</ConIndHEA96>
      <DiaLanIndAtDepHEA254>EN</DiaLanIndAtDepHEA254>
      <NCTSAccDocHEA601LNG>EN</NCTSAccDocHEA601LNG>
      <TotNumOfIteHEA305>1</TotNumOfIteHEA305>
      <TotNumOfPacHEA306>1</TotNumOfPacHEA306>
      <TotGroMasHEA307>1000</TotGroMasHEA307>
      <DecDatHEA383>20190912</DecDatHEA383>
      <DecPlaHEA394>DOVER</DecPlaHEA394>
      <DecPlaHEA394LNG>EN</DecPlaHEA394LNG>
    </HEAHEA>
    <TRAPRIPC1>
      <NamPC17>CITY WATCH SHIPPING</NamPC17>
      <StrAndNumPC122>125 Psuedopolis Yard</StrAndNumPC122>
      <PosCodPC123>SS99 1AA</PosCodPC123>
      <CitPC124>Ank-Morpork</CitPC124>
      <CouPC125>GB</CouPC125>
      <NADLNGPC>EN</NADLNGPC>
      <TINPC159>GB652420267000</TINPC159>
    </TRAPRIPC1>
    <TRACONCO1>
      <NamCO17>QUIRM ENGINEERING</NamCO17>
      <StrAndNumCO122>125 Psuedopolis Yard</StrAndNumCO122>
      <PosCodCO123>SS99 1AA</PosCodCO123>
      <CitCO124>Ank-Morpork</CitCO124>
      <CouCO125>GB</CouCO125>
      <TINCO159>GB602070107000</TINCO159>
    </TRACONCO1>
    <TRACONCE1>
      <NamCE17>DROFL POTTERY</NamCE17>
      <StrAndNumCE122>125 Psuedopolis Yard</StrAndNumCE122>
      <PosCodCE123>SS99 1AA</PosCodCE123>
      <CitCE124>Ank-Morpork</CitCE124>
      <CouCE125>GB</CouCE125>
      <NADLNGCE>EN</NADLNGCE>
      <TINCE159>GB658120050000</TINCE159>
    </TRACONCE1>
    <CUSOFFDEPEPT>
      <RefNumEPT1>GB000060</RefNumEPT1>
    </CUSOFFDEPEPT>
    <CUSOFFTRARNS>
      <RefNumRNS1>FR001260</RefNumRNS1>
      <ArrTimTRACUS085>201909160100</ArrTimTRACUS085>
    </CUSOFFTRARNS>
    <CUSOFFDESEST>
      <RefNumEST1>IT021100</RefNumEST1>
    </CUSOFFDESEST>
    <SEAINFSLI>
      <SeaNumSLI2>1</SeaNumSLI2>
      <SEAIDSID>
        <SeaIdeSID1>Seal001</SeaIdeSID1>
        <SeaIdeSID1LNG>EN</SeaIdeSID1LNG>
      </SEAIDSID>
    </SEAINFSLI>
    <GUAGUA>
      <GuaTypGUA1>3</GuaTypGUA1>
      <GUAREFREF>
        <GuaRefNumGRNREF1>default</GuaRefNumGRNREF1>
        <OthGuaRefREF4>EU_EXIT</OthGuaRefREF4>
        <AccCodREF6>test</AccCodREF6>
      </GUAREFREF>
    </GUAGUA>
    <GOOITEGDS>
      <IteNumGDS7>1</IteNumGDS7>
      <ComCodTarCodGDS10>default</ComCodTarCodGDS10>
      <DecTypGDS15>default</DecTypGDS15>
      <GooDesGDS23>Flowers</GooDesGDS23>
      <GooDesGDS23LNG>EN</GooDesGDS23LNG>
      <GroMasGDS46>1000</GroMasGDS46>
      <NetMasGDS48>999</NetMasGDS48>
      <CouOfDesGDS59>ex</CouOfDesGDS59>
      <PREADMREFAR2>
        <PreDocTypAR21>T2</PreDocTypAR21>
        <PreDocRefAR26>EU_EXIT-T2</PreDocRefAR26>
        <PreDocRefLNG>EN</PreDocRefLNG>
        <ComOfInfAR29>default</ComOfInfAR29>
        <ComOfInfAR29LNG>EN</ComOfInfAR29LNG>
      </PREADMREFAR2>
      <PRODOCDC2>
        <DocTypDC21>720</DocTypDC21>
        <DocRefDC23>EU_EXIT</DocRefDC23>
        <DocRefDCLNG>EN</DocRefDCLNG>
        <ComOfInfDC25>default</ComOfInfDC25>
        <ComOfInfDC25LNG>EN</ComOfInfDC25LNG>
      </PRODOCDC2>
      <PACGS2>
        <MarNumOfPacGS21>Bloomingales</MarNumOfPacGS21>
        <MarNumOfPacGS21LNG>EN</MarNumOfPacGS21LNG>
        <KinOfPacGS23>BX</KinOfPacGS23>
        <NumOfPacGS24>1</NumOfPacGS24>
      </PACGS2>
    </GOOITEGDS>
  </CC015B>

  val CD034A =
    <CD034A>
      <SynIdeMES1>UNOC</SynIdeMES1>
      <SynVerNumMES2>3</SynVerNumMES2>
      <MesSenMES3>MDTP-GUA-{UUID.randomUUID().toString.replaceAll("-", "").take(24)}</MesSenMES3>
      <MesRecMES6>NTA.GB</MesRecMES6>
      <DatOfPreMES9>{DateTimeFormatter.ofPattern("yyyyMMdd").format(OffsetDateTime.now(ZoneOffset.UTC))}</DatOfPreMES9>
      <TimOfPreMES10>{DateTimeFormatter.ofPattern("HHmm").format(OffsetDateTime.now(ZoneOffset.UTC))}</TimOfPreMES10>
      <IntConRefMES11>{Random.alphanumeric.take(14).mkString}</IntConRefMES11>
      <MesIdeMES19>{Random.alphanumeric.take(14).mkString}</MesIdeMES19>
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
          <AccCodCOD729>ABC1</AccCodCOD729>
        </ACCDOC728>
      </GUAREF2>
    </CD034A>

  val validDepartureHeaders: Seq[(String, String)] = Seq(
    "CustomProcessHost" -> "Digital",
    "X-Correlation-ID"  -> "137302f5-71ae-40a4-bd92-cac2ae7sde2f",
    "Date"              -> "Tue, 29 Sep 2020 11:46:50 +0100",
    "Content-Type"      -> "application/xml",
    "Accept"            -> "application/xml",
    "X-Message-Type"    -> "IE015",
    "X-Message-Sender"  -> "MDTP-000000000000000000000000011-01"
  )

  val validGuaranteeHeaders: Seq[(String, String)] = Seq(
    "CustomProcessHost" -> "Digital",
    "X-Correlation-ID"  -> "137302f5-71ae-40a4-bd92-cac2ae7sde2f",
    "Date"              -> "Tue, 29 Sep 2020 11:46:50 +0100",
    "Content-Type"      -> "application/xml",
    "Accept"            -> "application/xml",
    "X-Message-Type"    -> "IE034",
    "X-Message-Sender"  -> "MDTP-000000000000000000000000011-01"
  )

  val invalidHeaders: Seq[(String, String)] = Seq(
    "CustomProcessHost" -> "Digital",
    "X-Correlation-ID"  -> "137302f5-71ae-40a4-bd92-cac2ae7sde2f",
    "Date"              -> "Tue, 29 Sep 2020 11:46:50 +0100",
    "Content-Type"      -> "application/xml",
    "Accept"            -> "application/xml"
  )

  private def fakeValidXmlRequest(route: String, headers: Headers, body: Elem) =
    FakeRequest(method = "POST", uri = route, headers, body)

  private def fakeValidDepartureXmlRequest(route: String, bearerToken: String) =
    fakeValidXmlRequest(route, headers = FakeHeaders(validDepartureHeaders ++ Seq(HeaderNames.AUTHORIZATION -> s"Bearer $bearerToken")), body = CC015B)

  private def fakeValidGuaranteeXmlRequest(route: String, bearerToken: String) =
    fakeValidXmlRequest(route, headers = FakeHeaders(validGuaranteeHeaders ++ Seq(HeaderNames.AUTHORIZATION -> s"Bearer $bearerToken")), body = CD034A)

  private def fakeInvalidXmlRequest(route: String, bearerToken: String) =
    FakeRequest(method = "POST", uri = route, headers = FakeHeaders(invalidHeaders ++ Seq(HeaderNames.AUTHORIZATION -> s"Bearer $bearerToken")), body = CC015B)

  private def fakeUnauthorizedEmptyHeaderXmlRequest(route: String) =
    FakeRequest(method = "POST", uri = route, headers = FakeHeaders(validDepartureHeaders), body = CC015B)

  private def fakeUnauthorizedEmptyHeaderValueXmlRequest(route: String) =
    FakeRequest(method = "POST", uri = route, headers = FakeHeaders(validDepartureHeaders ++ Seq(HeaderNames.AUTHORIZATION -> s"")), body = CC015B)

  private def fakeUnauthorizedXmlRequest(route: String) =
    FakeRequest(method = "POST", uri = route, headers = FakeHeaders(validDepartureHeaders ++ Seq(HeaderNames.AUTHORIZATION -> s"Bearer 123")), body = CC015B)

  "gbpost" - {
    "POST IE015 with valid headers and valid bearer token" - {
      "should return 202 Accepted" in {
        val result = controller().gbpost(fakeValidDepartureXmlRequest(routes.StubController.gbpost.url, appConfig.eisgbBearerToken))
        status(result) mustEqual ACCEPTED
      }
    }

    "POST IE015 with invalid headers and valid bearer token" - {
      "should return 400 BadRequest" in {
        val result = controller().gbpost(fakeInvalidXmlRequest(routes.StubController.gbpost.url, appConfig.eisgbBearerToken))
        status(result) mustEqual BAD_REQUEST
      }
    }

    "POST IE015 with no Authorization header specified in request" - {
      "should return 403 Forbidden" in {
        val result = controller().gbpost(fakeUnauthorizedEmptyHeaderXmlRequest(routes.StubController.gbpost.url))
        status(result) mustEqual FORBIDDEN
      }
    }

    "POST IE015 with no value specified for Authorization header in request" - {
      "should return 403 Forbidden" in {
        val result = controller().gbpost(fakeUnauthorizedEmptyHeaderValueXmlRequest(routes.StubController.gbpost.url))
        status(result) mustEqual FORBIDDEN
      }
    }

    "POST IE015 with invalid bearer token" - {
      "should return 403 Forbidden" in {
        val result = controller().gbpost(fakeUnauthorizedXmlRequest(routes.StubController.gbpost.url))
        status(result) mustEqual FORBIDDEN
      }
    }

    "POST IE034 when a simulated response is returned" - {
      "should pass through success status from the response" in {
        val result =
          controller(response = Some(HttpResponse(OK, ""))).gbpost(fakeValidGuaranteeXmlRequest(routes.StubController.gbpost.url, appConfig.eisgbBearerToken))
        status(result) mustEqual OK
      }

      "should pass through client error status from the response" in {
        val result = controller(response = Some(HttpResponse(FORBIDDEN, "Argh!!!")))
          .gbpost(fakeValidGuaranteeXmlRequest(routes.StubController.gbpost.url, appConfig.eisgbBearerToken))
        status(result) mustEqual FORBIDDEN
      }

      "should pass through server error status from the response" in {
        val result = controller(response = Some(HttpResponse(GATEWAY_TIMEOUT, "Kaboom!!!")))
          .gbpost(fakeValidGuaranteeXmlRequest(routes.StubController.gbpost.url, appConfig.eisgbBearerToken))
        status(result) mustEqual GATEWAY_TIMEOUT
      }
    }
  }

  "nipost" - {
    "POST IE015 with valid headers and valid bearer token" - {
      "should return 202 Accepted" in {
        val result = controller().nipost(fakeValidDepartureXmlRequest(routes.StubController.nipost.url, appConfig.eisniBearerToken))
        status(result) mustEqual ACCEPTED
      }
    }

    "POST IE015 with invalid headers and valid bearer token" - {
      "should return 400 BadRequest" in {
        val result = controller().nipost(fakeInvalidXmlRequest(routes.StubController.nipost.url, appConfig.eisniBearerToken))
        status(result) mustEqual BAD_REQUEST
      }
    }

    "POST IE015 with no Authorization header specified in request" - {
      "should return 403 Forbidden" in {
        val result = controller().nipost(fakeUnauthorizedEmptyHeaderXmlRequest(routes.StubController.nipost.url))
        status(result) mustEqual FORBIDDEN
      }
    }

    "POST IE015 with no value specified for Authorization header in request" - {
      "should return 403 Forbidden" in {
        val result = controller().nipost(fakeUnauthorizedEmptyHeaderValueXmlRequest(routes.StubController.nipost.url))
        status(result) mustEqual FORBIDDEN
      }
    }

    "POST IE015 with invalid bearer token" - {
      "should return 403 Forbidden" in {
        val result = controller().nipost(fakeUnauthorizedXmlRequest(routes.StubController.nipost.url))
        status(result) mustEqual FORBIDDEN
      }
    }

    "POST IE034 when a simulated response is returned" - {
      "should pass through success status from the response" in {
        val result =
          controller(response = Some(HttpResponse(OK, ""))).nipost(fakeValidGuaranteeXmlRequest(routes.StubController.nipost.url, appConfig.eisniBearerToken))
        status(result) mustEqual OK
      }

      "should pass through client error status from the response" in {
        val result = controller(response = Some(HttpResponse(FORBIDDEN, "Argh!!!")))
          .nipost(fakeValidGuaranteeXmlRequest(routes.StubController.nipost.url, appConfig.eisniBearerToken))
        status(result) mustEqual FORBIDDEN
      }

      "should pass through server error status from the response" in {
        val result = controller(response = Some(HttpResponse(GATEWAY_TIMEOUT, "Kaboom!!!")))
          .nipost(fakeValidGuaranteeXmlRequest(routes.StubController.nipost.url, appConfig.eisniBearerToken))
        status(result) mustEqual GATEWAY_TIMEOUT
      }
    }
  }

  "notificationPost" - {
    "should return 200 OK" in {
      val result = controller().notificationPost(
        FakeRequest(
          method  = "POST",
          uri     = routes.StubController.notificationPost.url,
          headers = FakeHeaders(Seq()),
          body    = Json.obj("id" -> "TEST-ID", "messageCode" -> "IE015", "timestamp" -> "2021-12-01T08:58:46", "office" -> "GB")
        ))
      status(result) mustEqual OK
    }
  }
}
