import sbt._
import sbt.Keys._

object Dependencies {
  import Dependency._
  val akkademo = Seq(akkaActor, akkaTyped, akkaRemote, akkaJackson, logback, scalaTest)
}

object Dependency {
  object Version {
    val Akka      = "2.6.18"
    val Scalatest = "3.2.9"
  }

  // ---- Application dependencies ----

  val akkaActor   = "com.typesafe.akka"   %% "akka-actor"                 % Version.Akka
  val akkaTyped   = "com.typesafe.akka"   %% "akka-actor-typed"           % Version.Akka
  val akkaRemote  = "com.typesafe.akka"   %% "akka-remote"                % Version.Akka
  val akkaJackson = "com.typesafe.akka"   %% "akka-serialization-jackson" % Version.Akka
  val logback     = "ch.qos.logback"       % "logback-classic"            % "1.2.10"

  // ---- Test dependencies ----

  val scalaTest   = "org.scalatest"       %% "scalatest"               % Version.Scalatest  % "test"
}
