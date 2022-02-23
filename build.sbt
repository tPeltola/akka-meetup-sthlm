ThisBuild / organization := "akkademo"
ThisBuild / version      := "1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.15"

lazy val akkademo = (project in file("."))
  .aggregate(common, processor, service, client)
  .settings(defaultSettings)


lazy val common = (project in file("common"))
  .settings(defaultSettings ++ Seq(libraryDependencies ++= Dependencies.akkademo))

lazy val processor = (project in file("processor"))
  .dependsOn(common)
  .settings(defaultSettings ++ Seq(libraryDependencies ++= Dependencies.akkademo))

lazy val service = (project in file("service"))
  .dependsOn(common)
  .settings(defaultSettings ++ Seq(libraryDependencies ++= Dependencies.akkademo))

lazy val client = (project in file("client"))
  .dependsOn(common, service)
  .settings(defaultSettings ++ Seq(libraryDependencies ++= Dependencies.akkademo))

lazy val defaultSettings = Seq(
  // compile options
  scalacOptions ++= Seq("-encoding", "UTF-8", "-optimise", "-deprecation", "-unchecked"),
  javacOptions  ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),

  // disable parallel tests
  Test / parallelExecution := false
)