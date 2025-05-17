package models

/**
 * Represents a JSON boolean value.
 *
 * @property value the boolean value
 */
class JsonBoolean(val value: Boolean) : JsonValue() {

    /**
     * Serializes this boolean to its JSON string representation ("true" or "false").
     *
     * @return the JSON string for the boolean value
     */
    override fun toJsonString(): String = value.toString()

    /**
     * Accepts a JsonVisitor to process this boolean node.
     *
     * @param visitor the visitor handling this node
     */
    override fun accept(visitor: JsonVisitor) {
        visitor.visit(this)
    }
}