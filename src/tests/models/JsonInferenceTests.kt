package models.tests

import mappers.JsonMapper
import models.*
import models.toJsonValue
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for reflection-based JSON inference (Phase 2), verifying conversion of Kotlin values
 * and data classes into the JSON model.
 */
enum class TestEnum { A, B }

data class Simple(val x: Int, val y: String)
data class Nested(val inner: Simple, val flag: Boolean?)

enum class EvalType { TEST, PROJECT, EXAM }
data class EvalItem(
    val name: String,
    val percentage: Double,
    val mandatory: Boolean,
    val type: EvalType?
)
data class Course(
    val name: String,
    val credits: Int,
    val evaluation: List<EvalItem>
)

class JsonInferenceTest {

    /**
     * Verifies that a null value becomes JsonNull.
     */
    @Test
    fun nullBecomesJsonNull() {
        val json = (null as Any?).toJsonValue()
        assertSame(JsonNull, json)
    }

    /**
     * Verifies that a String becomes JsonString with the same content.
     */
    @Test
    fun stringBecomesJsonString() {
        val json = "hello".toJsonValue()
        assertTrue(json is JsonString)
        assertEquals("hello", (json as JsonString).value)
    }

    /**
     * Verifies that an Int becomes JsonNumber with the same integer value.
     */
    @Test
    fun intBecomesJsonNumber() {
        val json = 42.toJsonValue()
        assertTrue(json is JsonNumber)
        assertEquals(42, (json as JsonNumber).value)
    }

    /**
     * Verifies that a Double becomes JsonNumber with the same floating value.
     */
    @Test
    fun doubleBecomesJsonNumber() {
        val json = 3.14.toJsonValue()
        assertTrue(json is JsonNumber)
        assertEquals(3.14, (json as JsonNumber).value)
    }

    /**
     * Verifies that Boolean values become JsonBoolean with corresponding truth values.
     */
    @Test
    fun booleanBecomesJsonBoolean() {
        val jTrue = true.toJsonValue()
        val jFalse = false.toJsonValue()
        assertTrue(jTrue is JsonBoolean)
        assertTrue(jFalse is JsonBoolean)
        assertEquals(true, (jTrue as JsonBoolean).value)
        assertEquals(false, (jFalse as JsonBoolean).value)
    }

    /**
     * Verifies that an enum instance becomes a JsonString of its name.
     */
    @Test
    fun enumBecomesJsonStringOfName() {
        val json = TestEnum.B.toJsonValue()
        assertTrue(json is JsonString)
        assertEquals("B", (json as JsonString).value)
    }

    /**
     * Verifies that a List of mixed supported types becomes a JsonArray with correct element types.
     */
    @Test
    fun listOfMixedTypes() {
        val list: List<Any?> = listOf(1, "two", null, TestEnum.A)
        val json = list.toJsonValue()
        assertTrue(json is JsonArray)
        val elems = (json as JsonArray).elements
        assertEquals(4, elems.size)
        assertTrue(elems[0] is JsonNumber)
        assertTrue(elems[1] is JsonString)
        assertSame(JsonNull, elems[2])
        assertTrue(elems[3] is JsonString)
    }

    /**
     * Verifies that a Map<String, Any?> becomes a JsonObject with correct property types.
     */
    @Test
    fun mapStringToAnyBecomesJsonObject() {
        val map: Map<String, Any?> = mapOf("n" to 7, "s" to "x", "b" to false)
        val json = map.toJsonValue()
        assertTrue(json is JsonObject)
        val props = (json as JsonObject).properties
        assertEquals(3, props.size)
        assertTrue(props["n"] is JsonNumber)
        assertTrue(props["s"] is JsonString)
        assertTrue(props["b"] is JsonBoolean)
    }

    /**
     * Verifies that a simple data class infers to a JsonObject with matching properties.
     */
    @Test
    fun simpleDataClassInfersToJsonObject() {
        val obj = Simple(5, "five")
        val json = obj.toJsonValue()
        assertTrue(json is JsonObject)
        val props = (json as JsonObject).properties
        assertEquals(2, props.size)
        assertEquals(5, (props["x"] as JsonNumber).value)
        assertEquals("five", (props["y"] as JsonString).value)
    }

    /**
     * Verifies that nested data classes infer recursively and null optional values map to JsonNull.
     */
    @Test
    fun nestedDataClassInfersRecursively() {
        val nested = Nested(Simple(1, "one"), null)
        val json = nested.toJsonValue()
        assertTrue(json is JsonObject)
        val props = (json as JsonObject).properties
        assertTrue(props["inner"] is JsonObject)
        val innerProps = (props["inner"] as JsonObject).properties
        assertEquals(1, (innerProps["x"] as JsonNumber).value)
        assertEquals("one", (innerProps["y"] as JsonString).value)
        assertSame(JsonNull, props["flag"])
    }

    /**
     * Verifies that using an unsupported type throws IllegalStateException.
     */
    @Test(expected = IllegalStateException::class)
    fun unsupportedTypeThrows() {
        Any().toJsonValue()
    }

    /**
     * Verifies that a provided JsonMapper is applied after inference to transform values.
     */
    @Test
    fun mapperIsAppliedAfterInference() {
        val negMapper = object : JsonMapper() {
            override fun transformBoolean(jsonBoolean: JsonBoolean): JsonBoolean {
                return JsonBoolean(!jsonBoolean.value)
            }
        }
        val input = mapOf("a" to true, "b" to false)
        val json = input.toJsonValue(negMapper)
        val props = (json as JsonObject).properties
        assertEquals(false, (props["a"] as JsonBoolean).value)
        assertEquals(true, (props["b"] as JsonBoolean).value)
    }

    /**
     * Verifies the full Course/EvalItem example infers and serializes correctly.
     */
    @Test
    fun courseExampleInfersCorrectly() {
        val course = Course(
            "PA", 6, listOf(
                EvalItem("quizzes", 0.2, false, null),
                EvalItem("project", 0.8, true, EvalType.PROJECT)
            )
        )
        val json = course.toJsonValue()
        assertTrue(json is JsonObject)
        val props = (json as JsonObject).properties
        assertEquals("PA", (props["name"] as JsonString).value)
        assertEquals(6, (props["credits"] as JsonNumber).value)
        val evalArray = props["evaluation"] as JsonArray
        assertEquals(2, evalArray.elements.size)

        val first = evalArray.elements[0] as JsonObject
        val fprops = first.properties
        assertEquals("quizzes", (fprops["name"] as JsonString).value)
        assertEquals(0.2, (fprops["percentage"] as JsonNumber).value)
        assertEquals(false, (fprops["mandatory"] as JsonBoolean).value)
        assertSame(JsonNull, fprops["type"])

        val second = evalArray.elements[1] as JsonObject
        val sprops = second.properties
        assertEquals("project", (sprops["name"] as JsonString).value)
        assertEquals(0.8, (sprops["percentage"] as JsonNumber).value)
        assertEquals(true, (sprops["mandatory"] as JsonBoolean).value)
        assertTrue(sprops["type"] is JsonString)
        assertEquals("PROJECT", (sprops["type"] as JsonString).value)

        // Verify complete serialization matches the expected JSON
        val compact = json.toJsonString().replace("\\s+".toRegex(), "")
        val expectedJson = """{"name":"PA","credits":6,"evaluation":[{"name":"quizzes","percentage":0.2,"mandatory":false,"type":null},{"name":"project","percentage":0.8,"mandatory":true,"type":"PROJECT"}]}"""
        assertEquals(expectedJson, compact)
    }
}
