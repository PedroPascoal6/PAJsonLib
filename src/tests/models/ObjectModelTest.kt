package models.tests

import org.junit.Assert.*
import org.junit.Test
import models.*

/**
 * Unit tests for JsonObject behavior, including serialization and filtering.
 */
class ObjectModelTest {

    /**
     * Verifies that JsonObject serializes to a valid JSON object string containing all properties.
     */
    @Test
    fun testJsonObjectSerialization() {
        // given: a JsonObject with string and number properties
        val jsonObject = JsonObject(
            mapOf(
                "name" to JsonString("Alice"),
                "age" to JsonNumber(30)
            )
        )

        // when: serialize to JSON string
        val jsonStr = jsonObject.toJsonString()

        // then: string starts with '{' and ends with '}', and contains expected substrings
        assertTrue(jsonStr.startsWith("{") && jsonStr.endsWith("}"))
        assertTrue(jsonStr.contains("\"name\":"))
        assertTrue(jsonStr.contains("\"Alice\""))
        assertTrue(jsonStr.contains("\"age\":"))
        assertTrue(jsonStr.contains("30"))
    }

    /**
     * Verifies that JsonObject.filter returns a new object without the excluded entries.
     */
    @Test
    fun testJsonObjectFilter() {
        // given: an object with three properties
        val original = JsonObject(
            mapOf(
                "a" to JsonString("valueA"),
                "b" to JsonString("valueB"),
                "c" to JsonNumber(123)
            )
        )

        // when: filter out the entry with key "b"
        val filtered = original.filter { entry -> entry.key != "b" }

        // then: result does not contain key "b" and has two properties remaining
        assertFalse(filtered.properties.containsKey("b"))
        assertEquals(2, filtered.properties.size)
    }
}
