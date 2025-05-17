package models

/**
 * Represents the JSON null value.
 */
object JsonNull : JsonValue() {

    /**
     * Serializes this null to its JSON string representation.
     *
     * @return the string "null"
     */
    override fun toJsonString(): String = "null"

    /**
     * Accepts a JsonVisitor to process this null node.
     *
     * @param visitor the visitor handling this node
     */
    override fun accept(visitor: JsonVisitor) {
        visitor.visit(this)
    }
}