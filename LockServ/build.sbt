name := "lock-server"
version := "0.99"
scalaVersion := "2.11.8"
libraryDependencies ++= Seq(
"com.typesafe.akka" %% "akka-actor" % "2.4.8",
"com.typesafe.akka" %% "akka-testkit" % "2.4.8",
"org.scalatest" % "scalatest_2.11" % "2.2.5",
"com.typesafe.akka" % "akka-slf4j_2.11" % "2.4.8",
"ch.qos.logback" % "logback-classic" % "1.0.9"
) 
