import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin

val appName = "transits-movements-trader-at-departure-stub"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.12"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(inConfig(Test)(ScalafmtPlugin.scalafmtConfigSettings))
  .settings(inThisBuild(scalafmtOnCompile := true))
  .settings(scalacSettings)
  .settings(
    resolvers += Resolver.jcenterRepo,
    PlayKeys.playDefaultPort := 9491,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test
  )

lazy val scalacSettings = Def.settings(
  // Disable warnings arising from generated routing code
  scalacOptions += "-Wconf:src=routes/.*:silent",
  // Disable fatal warnings and warnings from discarding values
  scalacOptions ~= {
    opts =>
      opts.filterNot(Set("-Xfatal-warnings", "-Ywarn-value-discard", "-Ywarn-unused:params"))
  }
)
