import sbt.Keys._
import sbt._

object Common extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins      = plugins.JvmPlugin

  override lazy val projectSettings = Seq(
    organization := "com.firstbird",
    organizationName := "Firstbird GmbH",
    homepage := Some(url("https://github.com/firstbirdtech/backbone")),
    scmInfo := Some(ScmInfo(url("https://github.com/firstbirdtech/backbone"), "git@github.com:firstbirdtech/backbone.git")),
    developers += Developer("contributors", "Contributors", "hello@firstbird,com", url("https://github.com/firstbirdtech/backbone/graphs/contributors")),
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),

    scalaVersion := "2.11.8",
    scalacOptions ++= Seq(
      "-encoding",
      "UTF-8",
      "-feature",
      "-unchecked",
      "-deprecation",
      "-Xfatal-warnings",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Xfuture"
    ),
    javacOptions ++= Seq(
      "-Xlint:unchecked"
    ),
    // show full stack traces and test case durations
    testOptions in Test += Tests.Argument("-oDF"),
    resolvers ++= Seq(
      Resolver.mavenLocal
    )
  )
}
