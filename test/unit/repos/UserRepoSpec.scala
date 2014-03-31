package unit.repos

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import repos.UserRepo
import models.User
import common.TestHelpers.WithGraph
import org.anormcypher.Cypher
import java.util.Date
import play.api.libs.json.Json
import common.TestHelpers.fakeApplication
import java.util.UUID

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class UserRepoSpec extends PlaySpecification {

  "UserRepo" should {
    
    "user model serialize/deserialize" in new WithApplication {
    	val user = new User(
    	    id = UUID.randomUUID().toString(),
    		firstName = "kuku", 
    		lastName = "shmuku",
    		email = UUID.randomUUID().toString()+"@kuku.com",
    		created = new Date().getTime())
    	val str = Json.toJson(user)
    	val user2 = Json.fromJson[User](str).get
    	user.id must equalTo(user2.id)
    }
    
    
    
    "update user should return false if user doesn't exist" in new WithGraph {
    	val attrs = Map("name"->"myname", "last"->"myfamily")
    	val id = UUID.randomUUID().toString()
    	val resE = await(UserRepo.update(id, attrs))
    	resE must beLeft
    }
    
    /*"try" in new WithGraph {
    	val text = views.html.index("Your new application is ready.")
    	val id = "html"
    	val res = Cypher(
	        s"""create (u:User {id: '$id', text: '$text'}) return u"""
	        ).execute()
	    
	        
	    1  === 1    
    }*/
    
    "create user return true" in new WithGraph {
    	val id = UUID.randomUUID().toString()
    	val u = new User(id = id,
	    	firstName = "kuku", 
	    	lastName = "shmuku",
	    	email = id+"@kuku.com",
	    	created = new Date().getTime())
    	val resE = await(UserRepo.create(u))
    	resE must beRight
    	resE.right.get must beEqualTo(true)
    	val firstName  = Cypher(s"""match (u:User) where u.id='$id' return u.firstName""").apply().map(row => row[String]("u.firstName")).head
    	firstName must equalTo(u.firstName)
    }
    
    "update user should return true if user exists" in new WithGraph {
    	
    	val id = UUID.randomUUID().toString()
    	val firstName = "kuku" 
    	val lastName = "shmuku"
    	val email = id+"@kuku.com"
    	val created = new Date().getTime()
    	val res = Cypher(
	        s"""create (u:User {id: '$id', firstName: '$firstName', lastName: '$lastName', email: '$email', created: $created}) return u"""
	        ).execute()
	    
	    res must equalTo(true)
      
    	val attrs = Map("firstName"->"myname", "lastName"->"myfamily")
    	
    	val resE = await(UserRepo.update(id, attrs))
    	resE must beRight
    	
    	val email2  = Cypher(s"""match (u:User) where u.id='$id' return u.email""").apply().map(row => row[String]("u.email")).head
    	email2 must equalTo(email)
    	
    }
    
    "getByToken should return user if token exists" in new WithGraph {
    	val id = UUID.randomUUID().toString()
    	val firstName = "kuku" 
    	val lastName = "shmuku"
    	val email = id+"@kuku.com"
    	val created = new Date().getTime()
    	val res = Cypher(
	        s"""create (u:User {id: '$id', firstName: '$firstName', lastName: '$lastName', email: '$email', created: $created}) return u"""
	        ).execute()
	        
	    res must equalTo(true)
    	
    	val resE = await(UserRepo.setNewToken(email))
    	resE must beRight
    	val token = resE.right.get.token 
    	token must not beNone
    	
    	val userE = await(UserRepo.getByToken(token.get))
    	userE must beRight
    	userE.right.get.id must equalTo(id)
    }
    
    "setToken should fail if user doesn't exist" in new WithGraph {
    	
    	val resE = await(UserRepo.setNewToken(UUID.randomUUID().toString()+"@kuku.com"))
    	resE must beLeft
    }
    
    "setToken should update token if user exists" in new WithGraph {
    	val id = UUID.randomUUID().toString()
    	val firstName = "kuku" 
    	val lastName = "shmuku"
    	val email = id+"@kuku.com"
    	val created = new Date().getTime()
    	val res = Cypher(
	        s"""create (u:User {id: '$id', firstName: '$firstName', lastName: '$lastName', email: '$email', created: $created}) return u"""
	        ).execute()
	        
	    res must equalTo(true)
    	
    	val resE = await(UserRepo.setNewToken(email))
    	resE must beRight
    	resE.right.get.token must not beNone
    	
    	val token  = Cypher(s"""match (u:User) where u.id='$id' return u.token""").apply().map(row => row[String]("u.token")).head
    	token must not beEmpty
    }
    
    "get user should fail if no such user" in new WithGraph{
    	val user = await(UserRepo.get("kuku")) 
    	println(user.left.get.msg)
	    user must beLeft
    }
    
    "get user should return user" in new WithGraph{
      
    	val id = UUID.randomUUID().toString()
    	val firstName = "kuku" 
    	val lastName = "shmuku"
    	val email = id+"@kuku.com"
    	val created = new Date().getTime()
   
	    val res = Cypher(
	        s"""create (u:User {id: '$id', firstName: '$firstName', lastName: '$lastName', email: '$email', created: $created}) return u"""
	        ).execute()
	    
	    res must equalTo(true)
      
    	val userE = await(UserRepo.get(id)) 
	    userE must beRight
	    userE.right.get.email must equalTo(email)
    }

    "list all users should return list" in new WithGraph{
      
    	val id = UUID.randomUUID().toString()
    	val firstName = "kuku" 
    	val lastName = "shmuku"
    	val email = id+"@kuku.com"
    	val created = new Date().getTime()
   
	    val res = Cypher(
	        s"""create (u:User {id: '$id', firstName: '$firstName', lastName: '$lastName', email: '$email', created: $created}) return u"""
	        ).execute()
	    
	    res must equalTo(true)
	      
	    val usersE = await(UserRepo.list()) 
	    usersE must beRight
	    val users = usersE.right.get
	    users.length must equalTo(1)
    	val user = users(0)
	    user.id must equalTo(id)
    	user.role must beNone
      
    }
  }
}
