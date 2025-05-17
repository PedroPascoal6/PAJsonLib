package models.tests

import org.junit.Assert.assertEquals
import org.junit.Test
import models.*

/**
 * Unit tests for JsonNull serialization behavior.
 */
class NullModelTest {

    /**
     * Verifies that JsonNull serializes to the string "null".
     */
    @Test
    fun testJsonNullSerialization() {
        assertEquals("null", JsonNull.toJsonString())
    }
}
