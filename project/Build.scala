import sbt._
import Keys._
import PlayProject._

object  ApplicationBuild extends Build {

    val appName         = "spider"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "com.nowanswers" %% "chemistry" % "0.2.0",
      "com.typesafe.akka" % "akka-actor" % "2.0.2",
      "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2.1",
      "org.scalesxml" %% "scales-xml" % "0.4.3",
      "org.mockito" % "mockito-all" % "1.9.0" % "test"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(

      autoCompilerPlugins := true,

      libraryDependencies <+= scalaVersion { v =>
        compilerPlugin("org.scala-lang.plugins" % "continuations" % "2.9.2")
      },

      resolvers ++= Seq(
        Resolver.file("Local ivy Repository", file("/Users/ladlestein/.ivy2/local/"))(Resolver.ivyStylePatterns)
      )

    )

}

