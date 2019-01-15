package repository

class CustomerRepository(override var collectionName: String = "collection_name") : MongoRepository()