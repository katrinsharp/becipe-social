package adapters

import scala.concurrent.Future
import models.User
import scala.concurrent.ExecutionContext
import models.CustomError

trait UserAdapter {
	def list(): Future[Either[CustomError, Seq[User]]]
	def get(idAttrName: String, idAttrValue: String): Future[Either[CustomError, User]]
	def create(user: User): Future[Either[CustomError, Boolean]]
	def update(idAttrName: String, idAttrValue: String, attrs: Map[String, String]): Future[Either[CustomError, User]]
}