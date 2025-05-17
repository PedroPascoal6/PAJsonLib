package framework

/**
 * Marks a class or function with an HTTP route mapping.
 *
 * @property value the URI path or path pattern to map to this handler
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Mapping(val value: String, val method: String)

/**
 * Denotes a function parameter as a URI path variable.
 *
 * The parameter name must match the placeholder in the path pattern.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Path

/**
 * Denotes a function parameter as a query parameter.
 *
 * The parameter name is used to bind the URL query parameter value.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Param

/**
 * Marks a function parameter to receive the raw HTTP request body.
 *
 * When this annotation is present, the framework’s dispatcher should:
 * 1. Read the request body as a raw string (e.g., via `HttpServletRequest.reader.readText()`).
 * 2. Inject that string into the annotated parameter, allowing the handler to parse it
 *    into JSON, XML, or any custom format.
 *
 * Typical usage:
 * ```
 * @Mapping(value = "ints", method = "POST")
 * fun ints(@Body body: String): JsonArray {
 *     val array = (body.toJsonValue() as? JsonArray)
 *         ?: throw IllegalArgumentException("Expected a JSON array in the request body")
 *     // Process the array…
 * }
 * ```
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Body