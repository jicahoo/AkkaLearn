name := "AkkaInvestigation"

version := "0.1"

scalaVersion := "2.11.7"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies ++= Seq (
"com.typesafe.akka" %% "akka-actor" % "2.4-SNAPSHOT",
"com.typesafe.akka" %% "akka-testkit" % "2.4-SNAPSHOT",
"org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
)