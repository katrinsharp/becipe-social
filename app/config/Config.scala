package config

import play.api.Play.current
import org.anormcypher.Neo4jREST

object Config {
  
	private val config = play.api.Play.configuration
	
	val UNDEFINED = "undefined"
  
	val neo4jHost = config.getString("neo4j.host").getOrElse(UNDEFINED)
	val neo4jPort = config.getInt("neo4j.port").getOrElse(7474)
	
	println("neo4j.host: " + neo4jHost)
	println("neo4j.port: " + neo4jPort)
	
	println("Start configure...")
	
	Neo4jREST.setServer(neo4jHost, neo4jPort)
	
	println("Congifuration finished.")
}