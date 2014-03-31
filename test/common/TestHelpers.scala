package common

import play.api.test._
import play.api.test.Helpers._
import org.specs2.execute.AsResult
import play.api.GlobalSettings
import java.io.File
import play.api.Configuration
import play.api.Mode
import com.typesafe.config.ConfigFactory
import java.util.UUID
import org.neo4j.kernel.GraphDatabaseAPI
import org.neo4j.server.WrappingNeoServerBootstrapper
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import org.neo4j.server.configuration.ServerConfigurator
import org.neo4j.server.configuration.Configurator
import config.Config

object TestHelpers {
  
    def fakeGlobal = new GlobalSettings() {
	  override def onLoadConfig(config: Configuration, path: File, classloader: ClassLoader, mode: Mode.Mode): Configuration = {
    
	    val dbPortKey = "neo4j.port"
	    val dbPort = 7575
	    
	    val dbHostKey = "neo4j.host"
	    val dbHost = "localhost"
 
	    val envSpecificConfig = Configuration.from(Map(dbPortKey -> dbPort, dbHostKey -> dbHost)) ++ config
	    
	    super.onLoadConfig(envSpecificConfig, path, classloader, mode)	  
	  } 
    }
    
    def fakeApplication = new FakeApplication(withGlobal = Some(fakeGlobal))

	
	abstract class WithGraph extends WithApplication(fakeApplication) {
	    override def around[T: AsResult](t: => T) = super.around {
	   
	      val dbPath = System.getProperty("java.io.tmpdir") + UUID.randomUUID().toString().replace("-", "_") + "/graph.db"
	      val graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(dbPath).newGraphDatabase().asInstanceOf[GraphDatabaseAPI]	      
	      val config = new ServerConfigurator(graphDb);	      
	      config.configuration().setProperty(Configurator.WEBSERVER_PORT_PROPERTY_KEY, Config.neo4jPort)
	      val bootstrapper = new WrappingNeoServerBootstrapper(graphDb, config)
	      bootstrapper.start()
	      
	      val res = t

	      bootstrapper.stop()
	      graphDb.shutdown()
	      deleteFileOrDirectory(new File(dbPath))
	      res
	    }
	}
	
	def deleteFileOrDirectory(path: File): Unit = {
		if (path.exists()) {
			if (path.isDirectory())	{
				path.listFiles().foreach(child => if(child.exists())deleteFileOrDirectory(child))
			}
			try {
			  path.delete()
			} catch {
			  case _: Throwable =>
			}
		}
	}
}