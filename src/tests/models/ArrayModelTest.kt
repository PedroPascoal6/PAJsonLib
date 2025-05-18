package models.tests

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import models.*

/**
 * Unit tests for JsonArray behavior, including serialization, filtering, and mapping.
 */
class ArrayModelTest {

    /**
     * Verifies that JsonArray serializes to a valid JSON array string containing all elements.
     */
    @Test
    fun testJsonArraySerialization() {
        // given: a JsonArray with a number, a string, and null
        val jsonArray = JsonArray(listOf(JsonNumber(1), JsonString("two"), JsonNull))

        // when: serialize to JSON string
        val jsonStr = jsonArray.toJsonString()

        assertEquals("[1, \"two\", null]", jsonStr )
    }

    /**
     * Verifies that JsonArray.filter returns a new array with only matching elements.
     */
    @Test
    fun testJsonArrayFilter() {
        // given: a JsonArray with numbers and a string
        val array = JsonArray(
            listOf(
                JsonNumber(1),
                JsonNumber(2),
                JsonNumber(3),
                JsonString("not a number")
            )
        )

        // when: filter only JsonNumber elements
        val filteredArray = array.filter { it is JsonNumber }

        // then: result contains exactly three JsonNumber elements
        assertEquals(3, filteredArray.elements.size)
        filteredArray.elements.forEach { assertTrue(it is JsonNumber) }
    }

    /**
     * Verifies that JsonArray.map returns a new array with elements transformed by the provided function.
     */
    @Test
    fun testJsonArrayMap() {
        // given: a JsonArray of numbers
        val array = JsonArray(
            listOf(
                JsonNumber(1),
                JsonNumber(2),
                JsonNumber(3)
            )
        )

        // when: map each number to its double
        val mappedArray = array.map {
            if (it is JsonNumber) JsonNumber(it.value.toInt() * 2) else it
        }

        // then: result contains the doubled values [2,4,6]
        val expectedValues = listOf(2, 4, 6)
        mappedArray.elements.forEachIndexed { index, jsonValue ->
            assertTrue(jsonValue is JsonNumber)
            assertEquals(expectedValues[index], (jsonValue as JsonNumber).value.toInt())
        }
    }
}