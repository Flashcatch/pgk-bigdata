name := "pgk-bigdata"

version := "1.0"

lazy val `pgk-bigdata` = (project in file(".")).enablePlugins(PlayJava, PlayScala, LauncherJarPlugin)

javacOptions ++= Seq("-Xlint:all")

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "jitpack" at "https://jitpack.io"
resolvers += Resolver.mavenLocal

scalaVersion := "2.12.2"

libraryDependencies += guice
libraryDependencies += jcache
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test
libraryDependencies += "org.mybatis.caches" % "mybatis-ehcache" % "1.1.0"
libraryDependencies += "org.postgresql" % "postgresql" % "42.1.4"
libraryDependencies += "org.jsr107.ri" % "cache-annotations-ri-guice" % "1.0.0"
libraryDependencies += "io.swagger" %% "swagger-play2" % "1.6.0"
libraryDependencies += "org.projectlombok" % "lombok" % "1.16.18" % "provided"
libraryDependencies += "io.minio" % "minio" % "4.0.2"
libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala_2.12" % "2.9.6"
libraryDependencies += "org.apache.kudu" % "kudu-client" % "1.7.1"
// https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-client
libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "3.1.1"
// https://mvnrepository.com/artifact/org.apache.hive/hive-jdbc
libraryDependencies += "org.apache.hive" % "hive-jdbc" % "3.1.0"
// include play-redis library
libraryDependencies += "com.github.karelcemus" %% "play-redis" % "2.3.0"

libraryDependencies ++= Seq(
  javaJdbc, javaWs, cacheApi, ehcache, ws, evolutions,
  "org.mybatis" % "mybatis" % "3.3.1",
  "org.mybatis" % "mybatis-guice" % "3.10",
  "com.nimbusds" % "nimbus-jose-jwt" % "4.40", 
  "com.google.inject.extensions" % "guice-multibindings" % "4.1.0"
)

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
routesGenerator := InjectedRoutesGenerator

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

unmanagedResourceDirectories in Compile += (baseDirectory.value / "conf")