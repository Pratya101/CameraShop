name := "MiniCam"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  "mysql" % "mysql-connector-java" % "5.1.46",
  cache
)     

play.Project.playJavaSettings
