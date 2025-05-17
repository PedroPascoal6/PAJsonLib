package models.tests

import org.junit.Assert.assertEquals
import org.junit.Test
import models.*

/**
 * Unit tests for JsonBoolean serialization behavior.
 */
class BooleanModelTest {

    /**
     * Verifies that JsonBoolean serializes to "true" or "false" correctly.
     */
    @Test
    fun testJsonBooleanSerialization() {
        // given: true and false JsonBoolean instances
        val jsonTrue = JsonBoolean(true)
        val jsonFalse = JsonBoolean(false)

        // then: the JSON string matches the boolean literal
        assertEquals("true", jsonTrue.toJsonString())
        assertEquals("false", jsonFalse.toJsonString())
    }
}
