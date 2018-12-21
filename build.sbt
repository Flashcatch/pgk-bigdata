name := "pgk-bigdata"

version := "1.0"

lazy val `pgk-bigdata` = (project in file(".")).enablePlugins(PlayJava, PlayScala, LauncherJarPlugin)

javacOptions ++= Seq("-Xlint:all")

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "jitpack" at "https://jitpack.io"
resolvers += Resolver.mavenLocal

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  cacheApi,
  ehcache,
  guice,
  jcache
)

libraryDependencies += "io.swagger" %% "swagger-play2" % "1.6.0"
libraryDependencies += "org.projectlombok" % "lombok" % "1.16.18" % "provided"
libraryDependencies += "org.apache.kudu" % "kudu-client" % "1.7.1"
libraryDependencies += "com.google.inject.extensions" % "guice-multibindings" % "4.1.0"

libraryDependencies += "com.github.karelcemus" %% "play-redis" % "2.3.0"

// checkstyle settings
checkstyleSeverityLevel := Some(CheckstyleSeverityLevel.Error)
checkstyleConfigLocation := CheckstyleConfigLocation.File("checkstyle/checkstyle.xml")
checkstyleXsltTransformations := {
  Some(Set(
    CheckstyleXSLTSettings(baseDirectory(_ / "checkstyle/styles.xsl").value, target(_ / "checkstyle-report.html").value)
  ))
}
//(checkstyle in Compile) := (checkstyle in Compile).triggeredBy(compile in Compile).value

updateOptions := updateOptions.value.withCachedResolution(true)