name := "EIFG_Builder"

version := "1.0"

mainClass := Some("builder.EIFG_Builder")

libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-compiler" % "2.11.0",
    "org.scalatest" %% "scalatest" % "2.2.6" % "test",
    "org.json4s" %% "json4s-native" % "3.3.0",
    "org.json4s" %% "json4s-jackson" % "3.3.0"
)

connectInput in run := true

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

scalaVersion := "2.11.0"

trapExit := false
