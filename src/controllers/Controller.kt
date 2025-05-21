package controllers
import framework.Body
import models.*

import framework.Mapping
import framework.Path
import framework.Param
import mappers.JsonMapperString

/**
 * Controller for API endpoints under /api.
 */
@Mapping("api", "POST")
class Controller {

    /**
     * POST /api/ints
     *
     * Receives any JSON array and returns only the odd integer numbers.
     *
     * @param input the JsonValue from the request body (must be a JsonArray)
     * @return a JsonArray containing only the JsonNumber elements that are odd integers
     */
    @Mapping(value = "odd", method = "POST")
    fun odd(@Body input: JsonValue): JsonArray {
        val array = input as? JsonArray
            ?: throw IllegalArgumentException("Expected a JSON array in request body")

        val result = array.elements.mapNotNull { element ->
            if (element is JsonNumber) {
                val intValue = element.value.toInt()
                if (intValue % 2 != 0) {
                    // wrap back as JsonNumber to preserve JSON serialization
                    JsonNumber(intValue)
                } else null
            } else {
                null
            }
        }

        return JsonArray(result)
    }

    /**
     * POST /api/ints
     *
     * Receives any JSON array and returns only the integer numbers.
     *
     * @param input the JsonValue from the request body (must be a JsonArray)
     * @return a JsonArray containing only those JsonNumber elements whose value is an integer
     */
    @Mapping(value = "ints", method = "POST")
    fun ints(@Body input: JsonValue): JsonArray {
        val array = input as? JsonArray
            ?: throw IllegalArgumentException("Expected a JSON array in request body")

        val result = array.elements.mapNotNull { element ->
            if (element is JsonNumber) {
                val intValue = element.value.toInt()
                JsonNumber(intValue)
            } else {
                null
            }
        }

        return JsonArray(result)
    }


    /**
     * GET /api/pair
     *
     * Returns a Pair<String, String> via reflection.
     *
     * @return a Pair containing the strings "one" and "two"
     */
    @Mapping("pair", "POST")
    fun pair(): JsonObject {
        val map: Map<String, Any?> = mapOf("first" to "one", "second" to "two")
        return map.toJsonValue() as JsonObject
    }

    /**
     * GET /api/path/{pathVar}
     *
     * Returns the path variable appended with an exclamation mark.
     *
     * @param pathVar the path variable from the URL
     * @return the pathVar value with "!" appended
     */
    @Mapping("path/{pathVar}", "POST")
    fun path(@Path pathVar: String): String {
        return (pathVar.toJsonValue(JsonMapperString()) as JsonString).toString()
    }

    /**
     * GET /api/args?n={n}&text={text}
     *
     * Constructs a JsonObject dynamically with the given parameters.
     *
     * @param n the number of repetitions
     * @param text the base text to repeat
     * @return a JsonObject mapping the given text to its repeated form
     */
    @Mapping("args", "POST")
    fun args(@Param n: Int, @Param text: String): JsonValue {
        return mapOf(text to (text.toJsonValue() as JsonString).repeat(n)).toJsonValue()
    }
}
