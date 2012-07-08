import sbt._
import Keys._
import PlayProject._

object  ApplicationBuild extends Build {

    val appName         = "spider"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "com.typesafe.akka" % "akka-actor" % "2.0.2",
      "org.jsoup" % "jsoup" % "1.6.3",
      "org.specs2" %% "specs2" % "1.11" % "test"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
