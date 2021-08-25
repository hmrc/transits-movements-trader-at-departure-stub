import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-27"   % "5.12.0"
  )

  val test = Seq(
    "org.mockito"            % "mockito-core"          % "3.12.3",
    "org.scalatest"          %% "scalatest"            % "3.2.9",
    "com.typesafe.play"      %% "play-test"            % current,
    "org.scalatestplus.play" %% "scalatestplus-play"   % "4.0.3",
    "org.scalatestplus"      %% "mockito-3-2"          % "3.1.2.0",
    "org.scalacheck"         %% "scalacheck"           % "1.15.4",
    "com.github.tomakehurst" % "wiremock-standalone"   % "2.27.2",
    "com.vladsch.flexmark"   % "flexmark-all"          % "0.35.10"
  ).map(_ % "test")
}
