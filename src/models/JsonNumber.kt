package models

/**
 * Represents a JSON numeric value.
 *
 * @property value the numeric value (e.g., Int, Double)
 */
class JsonNumber(val value: Number) : JsonValue() {

    /**
     * Serializes this number to its JSON string representation.
     * Maintains separate logic for integer vs floating-point values.
     */
    override fun toJsonString(): String {
        return when (value) {
            is Byte, is Short, is Int, is Long -> value.toString()
            is Float, is Double -> {
                val d = value.toDouble()
                if (d % 1.0 == 0.0) d.toLong().toString() else d.toString()
            }
            else -> {
                // Fallback: treat as Double
                val d = value.toDouble()
                if (d % 1.0 == 0.0) d.toLong().toString() else d.toString()
            }
        }
    }

    /**
     * Accepts a JsonVisitor to process this numeric node.
     *
     * @param visitor the visitor handling this node
     */
    override fun accept(visitor: JsonVisitor) {
        visitor.visit(this)
    }
}