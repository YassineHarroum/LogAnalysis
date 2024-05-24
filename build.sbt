name := "LogAnalysis"

version := "0.1"

scalaVersion := "2.12.14"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.2.0",
  "org.apache.spark" %% "spark-sql" % "3.2.0",
  "org.jfree" % "jfreechart" % "1.5.3" // Adding JFreeChart dependency
)
