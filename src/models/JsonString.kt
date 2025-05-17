package models

/**
 * Represents a JSON string value.
 *
 * @property value the raw string content
 */
class JsonString(val value: String) : JsonValue() {

    /**
     * Serializes this string to its JSON string representation, escaping quotes.
     *
     * @return the JSON-quoted and escaped string
     */
    override fun toJsonString(): String = "\"${value.replace("\"", "\\\"")}\""

    /**
     * Accepts a JsonVisitor to process this string node.
     *
     * @param visitor the visitor handling this node
     */
    override fun accept(visitor: JsonVisitor) {
        visitor.visit(this)
    }
}
