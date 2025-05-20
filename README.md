# Advanced Programming Project 2024/2025

This repository contains two Kotlin modules for the Advanced Programming course:

1. **JSON Manipulation Library** (Phase 1 & 2)
2. **GetJson HTTP Framework**

---

## Part 1: JSON Manipulation Library

### Overview

A pure-Kotlin library for in-memory JSON modelling, manipulation, and serialization. No external dependencies are used, except JUnit for testing.

### Features

* **Model**: Classes for all JSON value types:

  * `JsonObject`
  * `JsonArray`
  * `JsonString`
  * `JsonNumber`
  * `JsonBoolean`
  * `JsonNull`
* **Filtering**:

  * `JsonObject.filter(predicate)` → new `JsonObject`
  * `JsonArray.filter(predicate)` → new `JsonArray`
* **Mapping**:

  * `JsonArray.map(transform)` → new `JsonArray`
* **Visitor Pattern**:

  * Traverse any JSON tree via `JsonVisitor` interface
  * Sample visitor implementations to:

    * Validate object keys
    * Ensure arrays are homogeneous (non-null)
* **Serialization**:

  * `toJsonString()` produces standard-compliant JSON text
* **Reflection-based Inference** (Phase 2)

  * Convert Kotlin data classes, lists, maps, enums, and primitives into the JSON model
  * Supports nested structures via recursive reflection

### Usage Example

```kotlin
// Programmatic construction
overval obj = JsonObject(mapOf(
  "name" to JsonString("Alice"),
  "age"  to JsonNumber(30)
))
println(obj.toJsonString()) // {"name":"Alice","age":30}

// Reflection-based inference
data class Course(
  val name: String,
  val credits: Int,
  val evaluation: List<EvalItem>
)
// ... define EvalItem and EvalType ...

val course = Course("PA", 6, listOf(
  EvalItem("quizzes", 0.2, false, null),
  EvalItem("project", 0.8, true, EvalType.PROJECT)
))
val inferred = course.toJsonValue()     // uses JsonMapper + reflection
println(inferred.toJsonString())
```

### Documentation

All public API elements are documented via **KDoc**. Consult the source for details.

### Testing

All unit tests are implemented using **JUnit** only, with no third-party libraries.
Tests can be executed with the Gradle wrapper included in this repository (no additional setup required):

**On Unix/macOS:**

```bash
./gradlew test
```

**On Windows (PowerShell):**

```powershell
gradlew.bat test
```

---

## Part 2: Express HTTP Framework

### Overview

A lightweight Kotlin framework for quickly creating HTTP API endpoints, fully integrated with the JSON Manipulation Library. It exposes endpoints via annotated Kotlin classes and methods, automatically handles path segments and query parameters, and serializes responses into JSON.

### Key Features

* **Annotation-driven routing**: Methods annotated with `@Mapping` become HTTP endpoints.
* **Parameter binding**: Supports binding of path variables (`@Path`), query parameters (`@Param`), and request bodies (`@Body`).
* **JSON serialization**: Responses are serialized using the integrated JSON library.
* **Error handling**: Automatically handles HTTP errors and serializes them into structured JSON responses, including error type and message.

### Available Endpoints

| HTTP Method | Path                       | Description                                                           | Example Response                 |
| ----------- | -------------------------- | --------------------------------------------------------------------- | -------------------------------- |
| POST        | `/api/odd`                 | Receives a JSON array, returns only odd integer numbers.              | `[1,3,5]`                        |
| POST        | `/api/ints`                | Receives a JSON array, returns only integer numbers.                  | `[1,2,3]`                        |
| POST        | `/api/pair`                | Returns a static JSON object representing a pair.                     | `{"first":"one","second":"two"}` |
| POST        | `/api/path/{pathVar}`      | Returns the provided path variable appended with an exclamation mark. | `"example!"`                     |
| POST        | `/api/args?n={n}&text={t}` | Returns a JSON object mapping `text` to itself repeated `n` times.    | `{"PA":"PAPAPA"}`                |

### Example Usage

**Server Initialization:**

```kotlin
fun main() {
  val app = GetJson(Controller::class)
  app.start(8080)
}
```

**Defining Controller Endpoints:**

```kotlin
@Mapping("api", "POST")
class Controller {

  @Mapping(value = "odd", method = "POST")
  fun odd(@Body input: JsonValue): JsonArray { /* implementation */ }

  @Mapping(value = "ints", method = "POST")
  fun ints(@Body input: JsonValue): JsonArray { /* implementation */ }

  @Mapping("pair", "POST")
  fun pair(): JsonObject { /* implementation */ }

  @Mapping("path/{pathVar}", "POST")
  fun path(@Path pathVar: String): String { /* implementation */ }

  @Mapping("args", "POST")
  fun args(@Param n: Int, @Param text: String): JsonValue { /* implementation */ }
}
```

### Error Handling

Errors are managed by the framework and returned as JSON:

```json
{
  "error": "Error message",
  "type": "BadRequestException"
}
```


### Testing

All integration tests for HTTP endpoints use **JUnit** and the Java HTTP client from the standard library (`java.net.HttpURLConnection`).

Run tests with Gradle:

```bash
gradlew test
```

---

## Project Structure

```
├── json-lib/           # JSON library module
│   ├── src/main/kotlin/models
│   ├── src/main/kotlin/mappers
│   └── src/test/kotlin
└── http-framework/     # GetJson framework module
    ├── src/main/kotlin/framework
    └── src/test/kotlin
```

## UML Class Diagram

![Diagram drawio](assets/Diagram.drawio.png)


## Release

* **JAR** artifacts published under `releases/`
* Versioned following Semantic Versioning (e.g., `v1.0.0`)

---

## License

MIT © 2025 Pedro Pascoal
