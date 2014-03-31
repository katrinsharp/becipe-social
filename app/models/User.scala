package models

import play.api.libs.json.Json
import org.anormcypher.CypherResultRow


case class User(
		id: String,
		firstName: String, 
		lastName: String, 
		email: String,
		role: Option[String] = None,
		token: Option[String] = None,
		created: Long)
		
object User {
	implicit val writes = Json.writes[User]
	implicit val reads = Json.reads[User]
}