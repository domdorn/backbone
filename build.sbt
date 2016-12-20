lazy val backbone = project.in(file(".")).aggregate(core)

lazy val core = project
  .in(file("backbone-core"))
  .settings(
    name := "backbone-core",
    libraryDependencies ++= Seq(
      "com.amazonaws"          % "aws-java-sdk-sqs"         % "1.11.51",
      "com.amazonaws"          % "aws-java-sdk-sns"         % "1.11.51",
      "com.typesafe.akka"      %% "akka-stream"             % "2.4.12",
      "com.lightbend.akka"     %% "akka-stream-alpakka-sqs" % "0.3",
      "com.typesafe.play"      %% "play-json"               % "2.5.10",
      "org.typelevel"          %% "cats"                    % "0.8.1",
      "org.scala-lang.modules" %% "scala-java8-compat"      % "0.8.0",
      "org.scalatest"          %% "scalatest"               % "3.0.1" % Test,
      "junit"                  % "junit"                    % "4.12" % Test
    )
  )
