import sbt._
import Keys._
import play.Project._

object  ApplicationBuild extends Build {

    val appName         = "spider"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "com.nowanswers" %% "chemistry" % "0.3.6",
      "com.typesafe.akka" %% "akka-actor" % "2.1.2",
      "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2.1" withSources(),
      "org.scalesxml" %% "scales-xml" % "0.4.5",
      "org.mongodb" %% "casbah" % "2.5.0",
      "com.novus" %% "salat" % "1.9.2-SNAPSHOT",
      "org.mockito" % "mockito-all" % "1.9.0" % "test"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(

      autoCompilerPlugins := true,

      scalaVersion := "2.10.1",

      resolvers ++= Seq(
        Resolver.file("Local ivy Repository", file("/Users/ladlestein/.ivy2/local/"))(Resolver.ivyStylePatterns),
        "Scala-Tools Maven2 Snapshots Repository" at "https://oss.sonatype.org/content/groups/scala-tools",
        "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

      )
    )



}

