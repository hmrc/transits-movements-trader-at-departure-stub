import uk.gov.hmrc.SbtArtifactory
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "transits-movements-trader-at-departure-stub"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala,
                 SbtAutoBuildPlugin,
                 SbtGitVersioning,
                 SbtDistributablesPlugin,
                 SbtArtifactory)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    majorVersion := 0,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test
  )
  .settings(publishingSettings: _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(PlayKeys.playDefaultPort := 9491)
  .settings(scalaVersion := "2.12.11")
  .settings(scalafmtOnCompile in ThisBuild := true)
