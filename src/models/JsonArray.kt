package models

/**
 * Represents a JSON array, holding a list of JsonValue elements.
 *
 * @property elements the list of values contained in this JSON array
 */
class JsonArray(val elements: List<JsonValue> = emptyList()) : JsonValue() {

    /**
     * Returns a new JsonArray containing only the elements matching the given predicate.
     *
     * @param predicate function to test each element for inclusion
     * @return a filtered JsonArray
     */
    fun filter(predicate: (JsonValue) -> Boolean): JsonArray {
        val filtered = elements.filter(predicate)
        return JsonArray(filtered)
    }

    /**
     * Returns a new JsonArray with each element transformed by the given function.
     *
     * @param transform function to apply to each element
     * @return a mapped JsonArray
     */
    fun map(transform: (JsonValue) -> JsonValue): JsonArray {
        val mapped = elements.map(transform)
        return JsonArray(mapped)
    }

    /**
     * Serializes this JSON array to its JSON string representation.
     *
     * @return the JSON string, e.g. ["a", 1, true]
     */
    override fun toJsonString(): String {
        val elems = elements.joinToString(", ") { it.toJsonString() }
        return "[$elems]"
    }

    /**
     * Accepts a JsonVisitor, invoking its visit method and propagating to child elements.
     *
     * @param visitor the visitor to process this array and its contents
     */
    override fun accept(visitor: JsonVisitor) {
        visitor.visit(this)
        elements.forEach { it.accept(visitor) }
    }
}
