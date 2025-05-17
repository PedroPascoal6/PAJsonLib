package mappers

import mappers.JsonMapper
import models.*
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for JsonMapper implementations, verifying identity and custom transformations.
 */
class JsonMapperTest {

    /**
     * Identity mapper using the default JsonMapper behavior (no transformations).
     */
    class IdentityMapper : JsonMapper()

    /**
     * Verifies that the identity mapper returns an unchanged JSON structure.
     */
    @Test
    fun testIdentityMapper() {
        // given: a complex JsonObject with nested values
        val original = JsonObject(
            mapOf(
                "num" to JsonNumber(10),
                "str" to JsonString("abc"),
                "arr" to JsonArray(listOf(JsonNumber(1), JsonNumber(2))),
                "obj" to JsonObject(mapOf("key" to JsonBoolean(false)))
            )
        )
        val mapper = IdentityMapper()

        // when: apply the identity mapping
        val mapped = mapper.map(original)

        // then: the JSON serialization remains identical
        assertEquals(original.toJsonString(), mapped.toJsonString())
    }

    /**
     * Custom mapper that doubles numeric values and uppercases string values.
     */
    class CustomMapper : JsonMapper() {
        override fun transformNumber(jsonNumber: JsonNumber): JsonNumber {
            // Multiply the numeric value by 2
            return JsonNumber(jsonNumber.value.toInt() * 2)
        }

        override fun transformString(jsonString: JsonString): JsonString {
            // Convert string to uppercase
            return JsonString(jsonString.value.uppercase())
        }
    }

    /**
     * Verifies that the custom mapper transforms numbers and strings as specified,
     * and applies transformations recursively to nested structures.
     */
    @Test
    fun testCustomMapper() {
        // given: a JsonObject with mixed nested arrays and objects
        val original = JsonObject(
            mapOf(
                "a" to JsonNumber(5),
                "b" to JsonString("hello"),
                "c" to JsonArray(
                    listOf(
                        JsonNumber(10),
                        JsonString("world")
                    )
                ),
                "d" to JsonObject(
                    mapOf("nested" to JsonNumber(3))
                )
            )
        )
        val mapper = CustomMapper()

        // when: apply the custom mapping
        val mapped = mapper.map(original)

        // then: construct the expected transformed JsonObject
        val expected = JsonObject(
            mapOf(
                "a" to JsonNumber(10),               // 5 * 2 = 10
                "b" to JsonString("HELLO"),        // "hello" -> "HELLO"
                "c" to JsonArray(
                    listOf(
                        JsonNumber(20),                // 10 * 2 = 20
                        JsonString("WORLD")           // "world" -> "WORLD"
                    )
                ),
                "d" to JsonObject(
                    mapOf("nested" to JsonNumber(6))  // 3 * 2 = 6
                )
            )
        )

        assertEquals(expected.toJsonString(), mapped.toJsonString())
    }
}
