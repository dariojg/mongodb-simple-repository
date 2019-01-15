import configurations.PORT
import org.bson.Document
import repository.CustomerRepository
import spark.Spark.port


fun main(args: Array<String>) {
    port(PORT)
    val repo = CustomerRepository()

    val result = repo.save(hashMapOf("test_string" to "value test", "test_integer" to 123, "test_double" to 22.11))
    println("result: ${result}")

    val finded: Document = repo.findById(result["_id"].toString())
    println("finded: $finded")
    repo.save(finded)

}