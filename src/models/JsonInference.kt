package models

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import mappers.JsonMapper

/**
 * Matches a data class constructor parameter to its corresponding property.
 *
 * @receiver the KClass of a data class
 * @param parameter the constructor parameter for which to find the property
 * @return the KProperty1 corresponding to the given parameter
 * @throws IllegalArgumentException if this KClass is not a data class or if the property is not found
 */
fun KClass<*>.matchProperty(parameter: KParameter): KProperty1<out Any, *> {
    require(isData) { "matchProperty can only be used on data classes" }
    return declaredMemberProperties.first { it.name == parameter.name }
}

/**
 * Infers a JsonValue representation of this object, using reflection for data classes,
 * and applies an optional JsonMapper for post-processing.
 *
 * Supported types:
 * - null → JsonNull
 * - JsonValue → itself
 * - String → JsonString
 * - Int, Double → JsonNumber
 * - Boolean → JsonBoolean
 * - Enum → JsonString of name
 * - List<Any?> → JsonArray
 * - Map<String, Any?> → JsonObject (keys must be Strings)
 * - Data classes (using primary constructor and properties)
 *
 * @receiver the object to convert to JsonValue
 * @param mapper an optional JsonMapper to apply transformations after inference
 * @return a JsonValue representing this object
 * @throws IllegalArgumentException if a Map key is not a String
 * @throws IllegalStateException if the data class has no primary constructor
 * @throws Error for unsupported types
 */
fun Any?.toJsonValue(mapper: JsonMapper = JsonMapper()): JsonValue {
    val raw: JsonValue = when {
        this == null -> JsonNull
        this is JsonValue -> this
        this is String -> JsonString(this)
        this is Int -> JsonNumber(this)
        this is Double -> JsonNumber(this)
        this is Boolean -> JsonBoolean(this)
        this is Enum<*> -> JsonString(this.name)
        this is List<*> -> {
            @Suppress("UNCHECKED_CAST")
            val list = this as List<Any?>
            JsonArray(list.map { it.toJsonValue() })
        }
        this is Map<*, *> -> {
            val entries = this.entries.associate { (k, v) ->
                require(k is String) { "Map keys must be Strings, but found ${k?.javaClass}" }
                k to v.toJsonValue()
            }
            JsonObject(entries)
        }
        this::class.isData -> run {
            val kclass = this::class
            val ctor = kclass.primaryConstructor
                ?: error("Data class without primary constructor: ${kclass.simpleName}")
            val propsMap = ctor.parameters.associate { param ->
                val prop = kclass.matchProperty(param)
                val value = prop.getter.call(this)
                param.name!! to value.toJsonValue(mapper)
            }
            JsonObject(propsMap)
        }
        else -> error("Unsupported type for JSON conversion: ${this.javaClass}")
    }
    return mapper.map(raw)
}

/**
 * Infers a JsonValue from this object and applies JsonVisitor to it.
 *
 * @receiver the object to convert and visit
 * @param visitor the JsonVisitor to apply
 * @param mapper an optional JsonMapper used during conversion
 */
fun Any?.acceptJson(visitor: JsonVisitor, mapper: JsonMapper = JsonMapper()) {
    val json = this.toJsonValue(mapper)
    json.accept(visitor)
}
