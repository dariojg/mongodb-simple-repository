package configurations

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.MongoSecurityException
import com.mongodb.client.MongoDatabase


object MongoClient {

    private val logger = mu.KotlinLogging.logger {}
    private const val connectionString = "mongodb://$database_user:$database_password@$uris/$database_name"
    private var client: MongoClient? = null
    private var codecRegistry = null

    init {
        try {
            logger.info { "Connection to mongo database" }

            val mongoClientURI = MongoClientURI(connectionString)
            this.client = MongoClient(mongoClientURI)
            logger.info("getting database $database_name")
        } catch (e: MongoSecurityException) {
            logger.error { "Error connecting databsae" }
            e.printStackTrace()
        }
    }

    fun getDatabase(): MongoDatabase {
        return client?.getDatabase(database_name)
            ?: throw IllegalStateException("Error getting database $database_name")
    }

//    MongoDB only creates an index if an index of the same specification does not already exist.
}
