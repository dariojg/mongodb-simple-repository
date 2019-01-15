package repository

import com.mongodb.MongoException
import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates.*
import com.mongodb.client.result.UpdateResult
import configurations.MongoClient
import exceptions.ResourceNotFound
import mu.KotlinLogging
import org.bson.BSONObject
import org.bson.BasicBSONObject
import org.bson.Document
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.omg.CosNaming.NamingContextPackage.NotFound
import java.time.LocalDateTime


abstract class MongoRepository {

    abstract var collectionName: String
    private val logger = KotlinLogging.logger {}
    private val db: MongoDatabase = MongoClient.getDatabase()

    fun insert(document: MutableMap<String, Any>): Any? {
        try {
            val collection = collection()
            val basicObject = Document(document).append("created", LocalDateTime.now())
            collection.insertOne(basicObject)
            return basicObject["_id"].toString()
        } catch (e: MongoException) {
            logger.error { "Error while inserting document $document" }
            e.printStackTrace()
            throw e
        }
    }

    fun save(document: MutableMap<String, Any>): Document {
        try {
            val collection = collection()
            val id = try {
                ObjectId(document.getOrDefault("_id", "").toString())
            } catch (e: IllegalArgumentException) {
                " " // No id
            }
            val mongoDocument = Document(document)
            val updatedResult: Document? = collection.findOneAndUpdate(
                Filters.eq("_id", id),
                combine(Document("\$set", mongoDocument), currentDate("lastModified"))
            )
            if (updatedResult == null) {
                collection.insertOne(
                    mongoDocument.append("created", LocalDateTime.now())
                )
                return mongoDocument
            }
            return updatedResult
        } catch (e: MongoException) {
            logger.error { "Error while saving document $document" }
            e.printStackTrace()
            throw e
        }

    }

    fun find(field: String, value: String): Document? {
        val collection = collection()
        return collection.find(Filters.eq(field, value)).first()
    }

    fun findById(value: String): Document {
        val collection = collection()
        return collection.find(Filters.eq("_id", ObjectId(value))).first() ?: throw  ResourceNotFound()
    }

    fun findAll(field: String, value: String): FindIterable<Document> {
        val collection = collection()
        return collection.find(Filters.eq(field, value))
    }

    private fun collection(): MongoCollection<Document> {
        return db.getCollection(collectionName)
    }
}