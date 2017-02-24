import sbt.{CrossVersion, Resolver}

sbtVersion := "0.13.13"

lazy val versions = new {
  val quill = "1.1.0"
}

unmanagedBase in "test" := baseDirectory.value / "lib"

lazy val commonSettings = Seq(
  organization := "github.Sergey778",
  scalaVersion := "2.11.8",
  version := "0.1.0",
  libraryDependencies += "com.twitter" %% "finagle-http" % "6.42.0",
  libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.1",
  libraryDependencies += "io.getquill" %% "quill-finagle-postgres" % versions.quill,
  addCompilerPlugin(
    "org.scalameta" % "paradise" % "3.0.0-M7" cross CrossVersion.full
  ),
  scalacOptions += "-Xplugin:macroparadise",
  // macroparadise plugin doesn't work in repl yet.
  scalacOptions in (Compile, console) := Seq(),
  // macroparadise doesn't work with scaladoc yet
  sources in (Compile, doc) := Nil,
  libraryDependencies += "org.scalameta" % "scalameta_2.11" % "1.6.0"
)

lazy val commonTestSettings = Seq(
  parallelExecution in test := false,
  fork in test := true,
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "io.getquill" %% "quill-async-postgres" % versions.quill % "test",
    "org.postgresql" % "postgresql" % "9.4.1208" % "test",
    "io.getquill" %% "quill-finagle-postgres" % versions.quill % "test"
  )
)

lazy val tablemagiclib = (project in file(".")).settings(commonSettings).dependsOn(tablemagic).aggregate(tablemagic)

lazy val tablemagic =
  (project in file("tablemagic"))
    .settings(
      commonSettings,
      commonTestSettings
      //unmanagedResources in Compile := Seq(baseDirectory.value / "src/resources/application.conf")
    )
    .dependsOn(tablemagicutil)
    .aggregate(tablemagicutil)

lazy val tablemagicutil =
  (project in file("tablemagicutil"))
    .settings(
      commonSettings,
      libraryDependencies += "io.getquill" %% "quill-jdbc" % versions.quill
    )
    .dependsOn(tablemagicfuture)
    .aggregate(tablemagicfuture)

lazy val tablemagicfuture =
  (project in file("tablemagicfuture")).settings(
    commonSettings,
    libraryDependencies += "org.scalameta" % "scalameta_2.11" % "1.6.0"
  )
