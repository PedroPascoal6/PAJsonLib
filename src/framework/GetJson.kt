package framework

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import models.JsonArray
import models.JsonBoolean
import models.JsonNull
import models.JsonNumber
import models.JsonString
import models.JsonValue
import models.toJsonValue
import mappers.JsonMapper
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.primaryConstructor
import java.net.InetSocketAddress
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.concurrent.Executors

/**
 * HTTP server that maps requests to controller functions and returns JSON responses.
 *
 * @param controllers one or more controller classes annotated with @Mapping
 */
class GetJson(vararg controllers: KClass<*>) {

    private data class Route(
        val segments: List<String>,
        val fn: KFunction<*>,
        val instance: Any,
        val params: List<KParameter>,
        val httpMethod: String
    )

    private val routes: List<Route>

    init {
        routes = controllers.flatMap { ctrl ->
            val base = ctrl.findAnnotation<Mapping>()?.value?.trim('/') ?: ""
            val instance = ctrl.primaryConstructor?.call()
                ?: error("Controller ${'$'}{ctrl.simpleName} requires a primary constructor with no arguments")

            ctrl.memberFunctions.mapNotNull { fn ->
                fn.findAnnotation<Mapping>()?.let { mapping ->
                    val fullPath = listOf(base, mapping.value.trim('/'))
                        .filter { it.isNotEmpty() }
                        .joinToString("/", prefix = "/")
                    val segments = fullPath.trim('/').split("/")
                    val userParams = fn.parameters.drop(1)
                    Route(segments, fn, instance, userParams, mapping.method.uppercase())
                }
            }
        }
    }

    /**
     * Starts the HTTP server on the specified port.
     */
    fun start(port: Int) {
        HttpServer.create(InetSocketAddress(port), 0).apply {
            createContext("/") { handle(it) }
            executor = Executors.newFixedThreadPool(4)
            start()
        }
        println("🚀 Listening on http://localhost:$port/")
    }

    /**
     * Handles an incoming HTTP exchange by matching the request to a route,
     * invoking the corresponding controller function, and writing the JSON response.
     */
    private fun handle(exchange: HttpExchange) {
        try {
            // split path
            val rawPath = exchange.requestURI.path.trim('/')
            val pathSegments = if (rawPath.isEmpty()) emptyList() else rawPath.split("/")

            // parse query params
            val queryMap = (exchange.requestURI.query ?: "")
                .split("&")
                .mapNotNull {
                    val parts = it.split("=", limit = 2)
                    if (parts.size == 2) parts[0] to URLDecoder.decode(parts[1], "UTF-8") else null
                }
                .toMap()

            // find route
            val route = routes.firstOrNull { r ->
                r.segments.size == pathSegments.size &&
                        r.segments.zip(pathSegments).all { (tpl, actual) -> tpl.startsWith("{") || tpl == actual }
            } ?: run {
                exchange.sendResponseHeaders(404, -1)
                return
            }

            // method check
            if (exchange.requestMethod.uppercase() != route.httpMethod) {
                exchange.sendResponseHeaders(405, -1)
                return
            }

            // extract path variables
            val pathVars = mutableMapOf<String, String>()
            route.segments.zip(pathSegments).forEach { (tpl, actual) ->
                if (tpl.startsWith("{")) {
                    val name = tpl.removeSurrounding("{", "}")
                    pathVars[name] = URLDecoder.decode(actual, "UTF-8")
                }
            }

            // build args
            val args = route.params.map { param ->
                when {
                    param.findAnnotation<Path>() != null -> convert(pathVars[param.name]!!, param)
                    param.findAnnotation<Param>() != null -> convert(queryMap[param.name]!!, param)
                    param.findAnnotation<Body>()  != null -> {
                        val bodyText = exchange.requestBody.bufferedReader(Charset.forName("UTF-8")).use { it.readText() }
                        parseJsonValue(bodyText)
                    }
                    else -> error("Parameter `${'$'}{param.name}` of `${'$'}{route.fn.name}` is missing @Path, @Param or @Body annotation")
                }
            }.toTypedArray()

            // invoke controller
            val result = route.fn.call(route.instance, *args)

            // serialize result
            val body = (result as Any?)
                .toJsonValue(JsonMapper())
                .toJsonString()
                .toByteArray(Charset.forName("UTF-8"))

            exchange.responseHeaders.add("Content-Type", "application/json; charset=utf-8")
            exchange.sendResponseHeaders(200, body.size.toLong())
            exchange.responseBody.use { it.write(body) }
        } catch (e: Exception) {
            e.printStackTrace()
            exchange.sendResponseHeaders(500, -1)
        } finally {
            exchange.close()
        }
    }

    /**
     * Converts a raw string to the expected type of the parameter.
     */
    private fun convert(raw: String, param: KParameter): Any {
        return when (param.type.classifier as KClass<*>) {
            Int::class    -> raw.toInt()
            Double::class -> raw.toDouble()
            Boolean::class-> raw.toBoolean()
            String::class -> raw
            else          -> error("Unsupported type for parameter `${'$'}{param.name}`: ${'$'}{param.type}")
        }
    }

    /**
     * Parses a JSON string (array or single value) into JsonValue, minimal support for tests.
     */
    private fun parseJsonValue(text: String): JsonValue {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) error("Request body JSON its empty")
        return if (trimmed.startsWith("[")) {
            // array
            val inner = trimmed.removePrefix("[").removeSuffix("]")
            val elements = mutableListOf<JsonValue>()
            var sb = StringBuilder()
            var inStr = false
            for (c in inner) {
                when {
                    c == '"' -> { inStr = !inStr; sb.append(c) }
                    c == ',' && !inStr -> {
                        elements.add(parseJsonValue(sb.toString())); sb = StringBuilder()
                    }
                    else -> sb.append(c)
                }
            }
            if (sb.isNotBlank()) elements.add(parseJsonValue(sb.toString()))
            JsonArray(elements)
        } else if (trimmed.startsWith("\"")) {
            JsonString(trimmed.removeSurrounding("\""))
        } else when (trimmed) {
            "null" -> JsonNull
            "true" -> JsonBoolean(true)
            "false" -> JsonBoolean(false)
            else -> {
                val num = trimmed.toDoubleOrNull() ?: error("Invalid JSON value: $trimmed")
                JsonNumber(num)
            }
        }
    }
}
