name := "scalajdbc"

version := "0.1"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc"               % "[1.7,)",
  "org.scalikejdbc" %% "scalikejdbc-interpolation" % "[1.7,)",
  "com.h2database"  %  "h2"                        % "[1.3,)",
  "ch.qos.logback"  %  "logback-classic"           % "[1.0,)",
  "org.apache.tomcat" % "tomcat-jdbc" % "7.0.47"
)

