lazy val root = (project in file(".")).
  settings(
    name := "hmrc-checkout",
    version := "1.0",
    scalaVersion := "2.12.1",
    libraryDependencies ++= Seq(
		"org.scalatest" %% "scalatest" % "3.0.1" % "test"
	)
  )

