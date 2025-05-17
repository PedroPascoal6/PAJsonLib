package controllers

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import models.*
import models.toJsonValue

/**
 * Unit tests for the Controller class, verifying JSON filtering and mapping endpoints.
 */
class ControllerTest {

    private val controller = Controller()

    /**
     * Verifies that 'odd' method filters only odd integer values from a JsonArray.
     */
    @Test
    fun testOddFiltersOnlyOddIntegers() {
        // given: mixed list including non-numbers and integers
        val input = listOf(1, "x", 2, 3, 4, 5).toJsonValue() as JsonArray

        // when: apply 'odd' filter
        val result = controller.odd(input)

        // then: result contains only JsonNumber elements and exactly the odd values
        assertTrue(result.elements.all { it is JsonNumber })
        assertEquals(3, result.elements.size)
        val values = result.elements.map { (it as JsonNumber).value.toInt() }
        assertEquals(listOf(1, 3, 5), values)
    }

    /**
     * Verifies that 'ints' method filters only integer values (including whole numbers in doubles).
     */
    @Test
    fun testIntsFiltersOnlyIntegers() {
        // given: mixed list with strings, integers, and fractional numbers
        val input = listOf(1, "a", 2, 3, "b", 4.0, 5.7).toJsonValue() as JsonArray

        // when: apply 'ints' filter
        val result = controller.ints(input)

        // then: result contains only JsonNumber and all integer values
        assertTrue(result.elements.all { it is JsonNumber })
        assertEquals(5, result.elements.size)
        val values = result.elements.map { (it as JsonNumber).value.toInt() }
        assertEquals(listOf(1, 2, 3, 4, 5), values)
    }

    /**
     * Verifies that 'pair' method returns a Pair of "one" and "two".
     */
    @Test
    fun testPairReturnsOneAndTwo() {
        // when: retrieve pair
        val pair = controller.pair()

        // then: first and second elements match expected values
        assertEquals("one", pair.first)
        assertEquals("two", pair.second)
    }

    /**
     * Verifies that 'path' method appends an exclamation mark to the input string.
     */
    @Test
    fun testPathAppendsExclamation() {
        // when & then: path method should append '!'
        assertEquals("hello!", controller.path("hello"))
    }

    /**
     * Verifies that 'args' method constructs a JsonObject with the provided text repeated n times.
     */
    @Test
    fun testArgsRepeatsTextNTimes() {
        // given: parameters for 'args'
        val n = 3
        val text = "ab"

        // when: call args to get a JsonObject
        val result = controller.args(n, text) as JsonObject

        // then: result matches expected JsonObject with repeated text
        val expected = mapOf(text to text.repeat(n)).toJsonValue() as JsonObject
        assertEquals(expected.toJsonString(), result.toJsonString())
    }
}