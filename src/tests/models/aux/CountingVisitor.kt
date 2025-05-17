import models.*

/**
 * A JsonVisitor implementation that counts how many nodes of each JSON type are visited.
 *
 * Tracks counts for objects, arrays, strings, numbers, booleans, and nulls.
 */
class CountingVisitor : JsonVisitor {

    /** Count of JsonObject nodes encountered during traversal. */
    var objectCount = 0

    /** Count of JsonArray nodes encountered during traversal. */
    var arrayCount = 0

    /** Count of JsonString nodes encountered during traversal. */
    var stringCount = 0

    /** Count of JsonNumber nodes encountered during traversal. */
    var numberCount = 0

    /** Count of JsonBoolean nodes encountered during traversal. */
    var booleanCount = 0

    /** Count of JsonNull nodes encountered during traversal. */
    var nullCount = 0

    /**
     * Called when a JsonObject node is visited.
     * Increments [objectCount].
     */
    override fun visit(jsonObject: JsonObject) {
        objectCount++
    }

    /**
     * Called when a JsonArray node is visited.
     * Increments [arrayCount].
     */
    override fun visit(jsonArray: JsonArray) {
        arrayCount++
    }

    /**
     * Called when a JsonString node is visited.
     * Increments [stringCount].
     */
    override fun visit(jsonString: JsonString) {
        stringCount++
    }

    /**
     * Called when a JsonNumber node is visited.
     * Increments [numberCount].
     */
    override fun visit(jsonNumber: JsonNumber) {
        numberCount++
    }

    /**
     * Called when a JsonBoolean node is visited.
     * Increments [booleanCount].
     */
    override fun visit(jsonBoolean: JsonBoolean) {
        booleanCount++
    }

    /**
     * Called when a JsonNull node is visited.
     * Increments [nullCount].
     */
    override fun visit(jsonNull: JsonNull) {
        nullCount++
    }
}
