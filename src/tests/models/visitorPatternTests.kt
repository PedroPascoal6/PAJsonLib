package models.tests

import CountingVisitor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import models.*
import models.toJsonValue
import models.acceptJson

/**
 * Tests for the Visitor pattern on JSON structures, including counting node types
 * and validating unique object keys.
 */
class VisitorPatternTest {

    /**
     * Verifies CountingVisitor correctly counts each JSON node type in a complex structure.
     */
    @Test
    fun testVisitorPattern() {
        // given: a JsonObject with mixed nested elements
        val json = JsonObject(
            mapOf(
                "name" to JsonString("Alice"),
                "age" to JsonNumber(30),
                "active" to JsonBoolean(true),
                "children" to JsonArray(listOf(JsonString("Bob"), JsonString("Carol"))),
                "address" to JsonNull
            )
        )
        val visitor = CountingVisitor()

        // when: traverse with visitor
        json.accept(visitor)

        // then: counts match expected occurrences
        assertEquals(1, visitor.objectCount)
        assertEquals(1, visitor.arrayCount)
        assertEquals(3, visitor.stringCount)
        assertEquals(1, visitor.numberCount)
        assertEquals(1, visitor.booleanCount)
        assertEquals(1, visitor.nullCount)
    }

    /**
     * Visitor that ensures each JsonObject has unique keys, throwing if duplicates are found.
     */
    class UniqueKeysValidatorVisitor : JsonVisitor {
        override fun visit(jsonObject: JsonObject) {
            val seenKeys = mutableSetOf<String>()
            for ((key, _) in jsonObject.properties) {
                if (!seenKeys.add(key)) {
                    throw IllegalStateException("Duplicate key found: $key")
                }
            }
        }
        override fun visit(jsonArray: JsonArray)   {}
        override fun visit(jsonString: JsonString) {}
        override fun visit(jsonNumber: JsonNumber) {}
        override fun visit(jsonBoolean: JsonBoolean) {}
        override fun visit(jsonNull: JsonNull)     {}
    }

    /**
     * Verifies that UniqueKeysValidatorVisitor passes a valid object without exception.
     */
    @Test
    fun uniqueKeysValidatorVisitorValid() {
        val json = JsonObject(mapOf("a" to JsonNumber(1), "b" to JsonString("x")))
        json.accept(UniqueKeysValidatorVisitor())
    }

    /**
     * Verifies that UniqueKeysValidatorVisitor detects duplicate keys and throws.
     */
    @Test(expected = IllegalStateException::class)
    fun uniqueKeysValidatorVisitorDetectsDuplicate() {
        // build a JsonObject with duplicate property names
        val duplicates = listOf("dup" to JsonNumber(1), "dup" to JsonNumber(2))
        val invalidMap: Map<String, JsonValue> = object : Map<String, JsonValue> {
            override val entries: Set<Map.Entry<String, JsonValue>> = duplicates.map { (k, v) ->
                object : Map.Entry<String, JsonValue> {
                    override val key: String = k
                    override val value: JsonValue = v
                }
            }.toSet()
            override val keys: Set<String> = entries.map { it.key }.toSet()
            override val values: Collection<JsonValue> = entries.map { it.value }
            override val size: Int = entries.size
            override fun containsKey(key: String) = keys.contains(key)
            override fun containsValue(value: JsonValue) = values.contains(value)
            override fun get(key: String) = entries.firstOrNull { it.key == key }?.value
            override fun isEmpty() = entries.isEmpty()
            fun iterator() = entries.iterator()
        }
        JsonObject(invalidMap).accept(UniqueKeysValidatorVisitor())
    }

    /**
     * Verifies CountingVisitor on JSON inferred from a simple data class.
     */
    @Test
    fun testCountingVisitorOnInferredJson() {
        data class SimpleDC(val a: Int, val b: String)
        val json = SimpleDC(10, "hi").toJsonValue()
        val visitor = CountingVisitor()
        json.accept(visitor)
        assertEquals(1, visitor.objectCount)
        assertEquals(0, visitor.arrayCount)
        assertEquals(1, visitor.stringCount)
        assertEquals(1, visitor.numberCount)
    }

    /**
     * Verifies extension function acceptJson performs inference then visits.
     */
    @Test
    fun testAcceptJsonExtension() {
        data class DC(val flag: Boolean)
        val visitor = CountingVisitor()
        DC(true).acceptJson(visitor)
        assertEquals(1, visitor.objectCount)
        assertEquals(1, visitor.booleanCount)
    }
}