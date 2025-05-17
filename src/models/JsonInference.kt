package models

import models.*
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import mappers.JsonMapper

fun KClass<*>.matchProperty(parameter: KParameter): KProperty1<out Any, *> {
    require(isData) { "matchProperty can only be used on data classes" }
    return declaredMemberProperties.first { it.name == parameter.name }
}

fun Any?.toJsonValue(mapper: JsonMapper = JsonMapper()): JsonValue {
    val raw = when {
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
            val entries = this.entries.map { (k, v) ->
                require(k is String) { "Chave de Map deve ser String, mas foi ${k?.javaClass}" }
                k to v.toJsonValue()
            }.toMap()
            JsonObject(entries)
        }

        this::class.isData -> run {
            val kclass = this::class
            val ctor   = kclass.primaryConstructor
                ?: error("Data class without primaryConstructor: ${kclass.simpleName}")

            val params: List<KParameter> = ctor.parameters
            val propsMap: Map<String, JsonValue> = params.map { param ->
                val prop = kclass.matchProperty(param)
                val value = prop.call(this)
                param.name!! to value.toJsonValue(mapper)
            }.toMap()

            JsonObject(propsMap)
        }
        else -> error("This type is not supported JSON: ${this.javaClass}")
    }

    return mapper.map(raw)
}

fun Any?.acceptJson(visitor: JsonVisitor, mapper: JsonMapper = JsonMapper()) {
    val json = this.toJsonValue(mapper)
    json.accept(visitor)
}
