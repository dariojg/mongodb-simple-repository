package exceptions

class MissingPropertyException(message: String = "no message") : Exception(message)

class ResourceNotFound(message: String = "Requested resource not found") : Exception(message)