name := "becipe-social"

version := "1.0-SNAPSHOT"

resolvers += "anormcypher" at "http://repo.anormcypher.org/"

resolvers += "neo4j-public-repository" at "http://m2.neo4j.org/content/groups/public"

libraryDependencies ++= Seq(
  //jdbc,
  //anorm,
  cache,
  "org.anormcypher" %% "anormcypher" % "0.4.4",
  "org.neo4j" % "neo4j" % "2.0.1" % "test",
  "org.neo4j.app" % "neo4j-server" % "2.0.1" % "test" classifier "static-web" classifier "",
  "com.sun.jersey" % "jersey-core" % "1.9" % "test"
)     


play.Project.playScalaSettings

ScoverageSbtPlugin.instrumentSettings

ScoverageSbtPlugin.ScoverageKeys.excludedPackages in ScoverageSbtPlugin.scoverage := "<empty>;Reverse.*;.*html"
