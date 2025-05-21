package mappers
import models.*

/**
 * Recursively applies transformations to a JsonValue tree.
 *
 * Subclasses can override specific transform methods to alter parts of the JSON structure.
 */
open class JsonMapperString : JsonMapper() {

    /**
     * Hook for transforming JsonString nodes. Default implementation returns the input unchanged.
     *
     * @param jsonString the JsonString value
     * @return the transformed JsonString
     */
    override fun transformString(jsonString: JsonString): JsonString {
        return jsonString.append("!")
    }

}
