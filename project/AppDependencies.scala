import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {
  val catsVersion = "2.6.1"

  val compile = Seq(
    "uk.gov.hmrc"   %% "bootstrap-backend-play-28" % "5.12.0",
    "uk.gov.hmrc"   %% "play-json-union-formatter" % "1.15.0-play-28",
    "io.lemonlabs"  %% "scala-uri"                 % "3.5.0",
    "org.typelevel" %% "cats-core"                 % catsVersion
  )

  val test = Seq(
    "org.mockito"             % "mockito-core"        % "3.12.3",
    "org.scalatest"          %% "scalatest"           % "3.2.9",
    "com.typesafe.play"      %% "play-test"           % current,
    "org.scalatestplus.play" %% "scalatestplus-play"  % "4.0.3",
    "org.scalatestplus"      %% "scalacheck-1-15"     % "3.2.9.0",
    "org.scalatestplus"      %% "mockito-3-2"         % "3.1.2.0",
    "com.github.tomakehurst"  % "wiremock-standalone" % "2.27.2",
    "com.vladsch.flexmark"    % "flexmark-all"        % "0.36.8"
  ).map(_ % "test")
}
