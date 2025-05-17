package models

/**
 * Visitor interface for traversing JSON structures using the Visitor pattern.
 */
interface JsonVisitor {
    /**
     * Visit a JsonObject node.
     * @param jsonObject the JsonObject being visited
     */
    fun visit(jsonObject: JsonObject)

    /**
     * Visit a JsonArray node.
     * @param jsonArray the JsonArray being visited
     */
    fun visit(jsonArray: JsonArray)

    /**
     * Visit a JsonString node.
     * @param jsonString the JsonString being visited
     */
    fun visit(jsonString: JsonString)

    /**
     * Visit a JsonNumber node.
     * @param jsonNumber the JsonNumber being visited
     */
    fun visit(jsonNumber: JsonNumber)

    /**
     * Visit a JsonBoolean node.
     * @param jsonBoolean the JsonBoolean being visited
     */
    fun visit(jsonBoolean: JsonBoolean)

    /**
     * Visit a JsonNull node.
     * @param jsonNull the JsonNull being visited
     */
    fun visit(jsonNull: JsonNull)
}

/**
 * Abstract base class for JSON value types.
 */
abstract class JsonValue {
    /**
     * Accept a [JsonVisitor] to process this node.
     * @param visitor the visitor to handle this node
     */
    abstract fun accept(visitor: JsonVisitor)

    /**
     * Serialize this value to its JSON string representation.
     * @return the JSON string for this value
     */
    abstract fun toJsonString(): String
}