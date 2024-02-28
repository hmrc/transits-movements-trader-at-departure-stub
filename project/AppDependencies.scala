import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {
  val catsVersion                  = "2.6.1"
  private val bootstrapPlayVersion = "8.4.0"

  val compile = Seq(
    "uk.gov.hmrc"   %% "bootstrap-backend-play-30" % bootstrapPlayVersion,
    "uk.gov.hmrc"   %% "play-json-union-formatter" % "1.21.0",
    "io.lemonlabs"  %% "scala-uri"                 % "4.0.3",
    "org.typelevel" %% "cats-core"                 % catsVersion
  )

  val test = Seq(
    "org.mockito"             % "mockito-core"           % "3.12.3",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "7.0.1",
    "org.scalatestplus"      %% "scalacheck-1-15"        % "3.2.9.0",
    "org.scalatestplus"      %% "mockito-4-2"            % "3.2.11.0",
    "uk.gov.hmrc"            %% "bootstrap-test-play-30" % bootstrapPlayVersion
  ).map(_ % Test)
}
