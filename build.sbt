name := """play-slick-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

routesGenerator := InjectedRoutesGenerator

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
//resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
//resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases/"
resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-slick" % "4.0.2",
    "com.typesafe.play" %% "play-slick-evolutions" % "4.0.2",
    "com.typesafe.play" %% "play-json" % "2.7.4",
    "com.h2database" % "h2" % "1.4.192",
    "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % "test",
    specs2 % Test,
    guice
)

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
sourceDirectories in (Compile, TwirlKeys.compileTemplates) :=
  (unmanagedSourceDirectories in Compile).value

fork in run := true
