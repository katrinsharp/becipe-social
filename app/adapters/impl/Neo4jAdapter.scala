package adapters.impl

import org.anormcypher.Cypher
import org.anormcypher.CypherResultRow
import org.anormcypher.CypherResultSetParser
import models.User
import org.anormcypher.CypherRow
import org.anormcypher.NeoNode
import play.api.libs.json.Json
import config.Contexts
import scala.concurrent.Future
import adapters.UserAdapter
import scala.concurrent.ExecutionContext
import models.CustomError



class Neo4jAdapter extends UserAdapter {
	
	implicit val execctx = Contexts.httpExecutionContext
	
	implicit def cypherResultRow2User(row: org.anormcypher.CypherResultRow) = {
		new User(
	        row[String]("u.id"), 
	        row[String]("u.firstName"), 
	        row[String]("u.lastName"),
	        row[String]("u.email"),
	        row[Option[String]]("u.role"),
	        row[Option[String]]("u.token"),
	        row[Long]("u.created"))
	}
	implicit def fromOptionToOption[CypherResultRow, User]
		(from: Option[org.anormcypher.CypherResultRow]): Option[models.User] = from.map(cypherResultRow2User(_))
		

	private val u = "u.id, u.firstName, u.lastName, u.email, u.role, u.token, u.created"	
	
	def create(u: User): Future[Either[CustomError, Boolean]] = {
		
	  Future {		
			throwableToLeft({
			  
				Cypher(s"""create (
				    u:User {id: '${u.id}', 
					firstName: '${u.firstName}', 
					lastName: '${u.lastName}', 
					email: '${u.email}', 
					created: ${u.created}})"""
	        ).execute()	
			})
		}	
	}
	
	def update(idAttrName: String, idAttrValue: String, attrs: Map[String, String]): Future[Either[CustomError, User]] = {
		Future {		
			throwableToLeft({
			  
				val query = attrs.foldLeft(s"match (u:User) where u.$idAttrName='$idAttrValue' set ")(
				    (acc, attr) => acc + "u." + attr._1 + "='" + attr._2 + "',").dropRight(1) + 
				    "return " + u
				    
				Cypher(query).apply().toSeq.head
			})
		}		
	}
	
	def get(idAttrName: String, idAttrValue: String): Future[Either[CustomError, User]] = {
		Future {		
			throwableToLeft({
		  
				val query = s"match (u:User) where u.$idAttrName='$idAttrValue' return " + u  
				Cypher(query).apply().toSeq.head
			})
		}	
	}
	
	def list() = {
		Future {			
			throwableToLeft({
		  
				val query = "match (u:User) return " + u  
				Cypher(query).apply().toSeq.map(cypherResultRow2User)
			})
		}
	}
	
	def throwableToLeft[T](block: => T): Either[CustomError, T] =
	  try {
	    Right(block)
	  } catch {
	    case ex: Throwable if ex.getMessage() contains "head of empty stream" => Left(new CustomError(500, "Such a user does not exist"))
	    case ex: Throwable => Left(new CustomError(500, ex.getMessage()))
	  }
}

