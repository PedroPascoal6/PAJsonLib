package mappers
import models.*

/**
 * Recursively applies transformations to a JsonValue tree.
 *
 * Subclasses can override specific transform methods to alter parts of the JSON structure.
 */
open class JsonMapper {

    /**
     * Recursively maps each node in the JSON structure, then applies type-specific transforms.
     *
     * @param json the root JsonValue to transform
     * @return a new JsonValue with transformations applied
     */
    fun map(json: JsonValue): JsonValue = when (json) {
        is JsonObject -> {
            // First recurse into children, then apply object-level transform
            JsonObject(json.properties.mapValues { map(it.value) })
                .let { transformObject(it) }
        }
        is JsonArray -> {
            // First recurse into elements, then apply array-level transform
            JsonArray(json.elements.map { map(it) })
                .let { transformArray(it) }
        }
        is JsonString -> transformString(json)
        is JsonNumber -> transformNumber(json)
        is JsonBoolean -> transformBoolean(json)
        is JsonNull -> transformNull(json)
        else -> json
    }

    /**
     * Hook for transforming JsonObject nodes. Default implementation returns the input unchanged.
     *
     * @param jsonObject the JsonObject after recursive mapping
     * @return the transformed JsonObject
     */
    open fun transformObject(jsonObject: JsonObject): JsonObject = jsonObject

    /**
     * Hook for transforming JsonArray nodes. Default implementation returns the input unchanged.
     *
     * @param jsonArray the JsonArray after recursive mapping
     * @return the transformed JsonArray
     */
    open fun transformArray(jsonArray: JsonArray): JsonArray = jsonArray

    /**
     * Hook for transforming JsonString nodes. Default implementation returns the input unchanged.
     *
     * @param jsonString the JsonString value
     * @return the transformed JsonString
     */
    open fun transformString(jsonString: JsonString): JsonString = jsonString

    /**
     * Hook for transforming JsonNumber nodes. Default implementation returns the input unchanged.
     *
     * @param jsonNumber the JsonNumber value
     * @return the transformed JsonNumber
     */
    open fun transformNumber(jsonNumber: JsonNumber): JsonNumber = jsonNumber

    /**
     * Hook for transforming JsonBoolean nodes. Default implementation returns the input unchanged.
     *
     * @param jsonBoolean the JsonBoolean value
     * @return the transformed JsonBoolean
     */
    open fun transformBoolean(jsonBoolean: JsonBoolean): JsonBoolean = jsonBoolean

    /**
     * Hook for transforming JsonNull nodes. Default implementation returns the input unchanged.
     *
     * @param jsonNull the JsonNull value
     * @return the transformed JsonValue (usually JsonNull)
     */
    open fun transformNull(jsonNull: JsonNull): JsonValue = jsonNull
}
