package models.tests

import org.junit.Assert.assertEquals
import org.junit.Test
import models.*

/**
 * Unit tests for JsonString serialization, including escaping of quotes.
 */
class ModelStringTest {

    /**
     * Verifies that JsonString serializes a simple string with surrounding quotes.
     */
    @Test
    fun testJsonStringSerialization() {
        val jsonString = JsonString("Hello")
        assertEquals("\"Hello\"", jsonString.toJsonString())
    }

    /**
     * Verifies that JsonString correctly escapes internal quotation marks.
     */
    @Test
    fun testJsonStringSerializationWithQuotes() {
        val jsonString = JsonString("He said \"Hello\"")
        // Check if internal quotes are properly escaped
        assertEquals("\"He said \\\"Hello\\\"\"", jsonString.toJsonString())
    }
}
