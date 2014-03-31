package repos

import scala.concurrent.Future
import config.Contexts
import models.User
import adapters.impl.Neo4jAdapter
import adapters.UserAdapter
import models.CustomError
import java.util.UUID

object UserRepo{
  
	val adapter: UserAdapter = new Neo4jAdapter
	
	def list(): Future[Either[CustomError, Seq[User]]] = {
		  adapter.list()
	}
	
	def create(user: User): Future[Either[CustomError, Boolean]] = {
		  adapter.create(user)
	}
	
	def get(id: String): Future[Either[CustomError, User]] = {
		  adapter.get("id", id)
	}
	
	def getByToken(token: String): Future[Either[CustomError, User]] = {
		  adapter.get("token", token)
	}
	
	def update(id: String, attrs: Map[String, String]): Future[Either[CustomError, User]] = {
		adapter.update("id", id, attrs)
	} 
	
	def setNewToken(email: String): Future[Either[CustomError, User]] = {
		adapter.update("email", email, Map("token"->UUID.randomUUID().toString()))
	}
}