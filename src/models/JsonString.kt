package models

/**
 * Represents a JSON string value.
 *
 * @property value the raw string content
 */
class JsonString(var value: String) : JsonValue() {

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

    /**
     * Repeats x times the value on string like ("text" as string).repeat(n)
     *
     * @param n number of repeats
     */
    fun repeat(n:Int): String {
        return List(n) { value }.joinToString("")
    }

    /**
     * Append to current value other string
     */
    fun append(str:String): JsonString {
         value = value + str
        return this
    }

    /**
     * Convert to String
     */
    override fun toString():String{
        return value
    }
}
