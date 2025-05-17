package models

/**
 * Represents a JSON object, holding a map of property names to JsonValue instances.
 *
 * @property properties the map of key-value pairs for this JSON object
 */
class JsonObject(val properties: Map<String, JsonValue> = emptyMap()) : JsonValue() {

    /**
     * Returns a new JsonObject containing only the entries matching the given predicate.
     *
     * @param predicate function to test each map entry for inclusion
     * @return a filtered JsonObject
     */
    fun filter(predicate: (Map.Entry<String, JsonValue>) -> Boolean): JsonObject {
        val filtered = properties.filter(predicate)
        return JsonObject(filtered)
    }

    /**
     * Serializes this JSON object to its JSON string representation.
     *
     * @return the JSON string, e.g. {"key": "value", "num": 1}
     */
    override fun toJsonString(): String {
        val entries = properties.map { (key, value) ->
            "\"${key.replace("\"", "\\\"")}\": ${value.toJsonString()}"
        }.joinToString(", ")
        return "{$entries}"
    }

    /**
     * Accepts a JsonVisitor, invoking its visit method and propagating to child values.
     *
     * @param visitor the visitor to process this object and its values
     */
    override fun accept(visitor: JsonVisitor) {
        visitor.visit(this)
        properties.values.forEach { it.accept(visitor) }
    }
}
