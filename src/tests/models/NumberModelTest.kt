package models.tests

import org.junit.Assert.assertEquals
import org.junit.Test
import models.*

/**
 * Unit tests for JsonNumber serialization behavior.
 */
class NumberModelTest {

    /**
     * Verifies that JsonNumber serializes its numeric value correctly.
     */
    @Test
    fun testJsonNumberSerialization() {
        val jsonNumber = JsonNumber(123)
        assertEquals("123", jsonNumber.toJsonString())
    }
}
