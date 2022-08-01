name := "EIFG_Builder"

version := "1.0"

//mainClass := Some("builder.EIFG_Builder")

libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-compiler" % "2.11.0",
    "org.scalatest" %% "scalatest" % "2.2.6" % "test",
    "org.json4s" %% "json4s-native" % "3.3.0",
    "org.json4s" %% "json4s-jackson" % "3.3.0",
    "org.reflections" % "reflections" % "0.10.2",
//    "com.typesafe.akka" %% "akka-actor" % "2.4.17",
//    "com.typesafe.akka" %% "akka-agent" % "2.4.17",
//    "com.typesafe.akka" %% "akka-camel" % "2.4.17",
//    "com.typesafe.akka" %% "akka-cluster" % "2.4.17",
//    "com.typesafe.akka" %% "akka-cluster-metrics" % "2.4.17",
//    "com.typesafe.akka" %% "akka-cluster-sharding" % "2.4.17",
//    "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.17",
//    "com.typesafe.akka" %% "akka-contrib" % "2.4.17",
//    "com.typesafe.akka" %% "akka-multi-node-testkit" % "2.4.17",
//    "com.typesafe.akka" %% "akka-osgi" % "2.4.17",
//    "com.typesafe.akka" %% "akka-persistence" % "2.4.17",
//    "com.typesafe.akka" %% "akka-persistence-tck" % "2.4.17",
//    "com.typesafe.akka" %% "akka-remote" % "2.4.17",
//    "com.typesafe.akka" %% "akka-slf4j" % "2.4.17",
//    "com.typesafe.akka" %% "akka-stream" % "2.4.17",
//    "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.17",
//    "com.typesafe.akka" %% "akka-testkit" % "2.4.17",
//    "com.typesafe.akka" %% "akka-distributed-data-experimental" % "2.4.17",
//    "com.typesafe.akka" %% "akka-typed-experimental" % "2.4.17",
//    "com.typesafe.akka" %% "akka-persistence-query-experimental" % "2.4.17"
)

//connectInput in run := true

scalaVersion := "2.11.12"

//trapExit := false
