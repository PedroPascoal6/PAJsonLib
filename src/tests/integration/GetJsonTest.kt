package controllers

import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.ServerSocket
import java.net.URL
import controllers.Controller
import framework.GetJson

/**
 * Integration tests for the GetJson HTTP framework, using only JUnit and HttpURLConnection.
 */
class GetJsonTest {

    companion object {
        private lateinit var server: GetJson
        private var port: Int = 0

        /**
         * Sets up the HTTP server on a random free port before any tests run.
         */
        @BeforeClass
        @JvmStatic
        fun setup() {
            // Acquire a free port
            ServerSocket(0).use { socket -> port = socket.localPort }
            // Start the server with the Controller under test
            server = GetJson(Controller::class)
            server.start(port)
        }
    }

    /**
     * Performs an HTTP request to the running server and returns the status code and body.
     *
     * @param path the request path (including query string if any)
     * @param method the HTTP method to use (default: GET)
     * @param body the optional request body for POST/PUT
     * @return a Pair of (HTTP status code, response body as string)
     */
    private fun httpRequest(path: String, method: String = "GET", body: String? = null): Pair<Int, String> {
        val url = URL("http://localhost:$port$path")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = method
        if (body != null) {
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.outputStream.use { os ->
                os.write(body.toByteArray(Charsets.UTF_8))
            }
        }
        conn.connect()
        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val respBody = stream?.let {
            BufferedReader(InputStreamReader(it)).use { reader ->
                reader.lineSequence().joinToString("\n")
            }
        } ?: ""
        conn.disconnect()
        return code to respBody
    }

    /**
     * Verifies that POST /api/ints filters only integer values from a JSON array.
     */
    @Test
    fun testIntsEndpoint() {
        // given: mixed list with strings, integers, and fractional numbers
        val inputJson = "[1, \"a\", 2, 3, \"b\", 4.0, 5.7]"
        val (code, body) = httpRequest("/api/ints", method = "POST", body = inputJson)
        assertEquals(200, code)
        val compact = body.replace(Regex("\\s+"), "")
        assertEquals("[1,2,3,4,5]", compact)
    }

    /**
     * Verifies that POST /api/pair returns a JSON object with "first":"one" and "second":"two".
     */
    @Test
    fun testPairEndpoint() {
        val (code, body) = httpRequest("/api/pair", method = "POST")
        assertEquals(200, code)
        val compact = body.replace(Regex("\\s+"), "")
        assertEquals("{\"first\":\"one\",\"second\":\"two\"}", compact)
    }

    /**
     * Verifies that GET /api/path/a and /api/path/b return the path variable with an exclamation.
     */
    @Test
    fun testPathEndpoint() {
        val (codeA, bodyA) = httpRequest("/api/path/a", method = "POST")
        assertEquals(200, codeA)
        assertEquals("\"a!\"", bodyA.trim())

        val (codeB, bodyB) = httpRequest("/api/path/b", method = "POST")
        assertEquals(200, codeB)
        assertEquals("\"b!\"", bodyB.trim())
    }

    /**
     * Verifies that GET /api/args?n=3&text=PA returns {"PA":"PAPAPA"}.
     */
    @Test
    fun testArgsEndpoint() {
        val (code, body) = httpRequest("/api/args?n=3&text=PA", method = "POST")
        assertEquals(200, code)
        val compact = body.replace(Regex("\\s+"), "")
        assertEquals("{\"PA\":\"PAPAPA\"}", compact)
    }

    /**
     * Verifies that unknown paths return HTTP 404 Not Found.
     */
    @Test
    fun testNotFound() {
        val (code, _) = httpRequest("/api/unknown")
        assertEquals(404, code)
    }

    /**
     * Verifies that unsupported HTTP methods return HTTP 405 Method Not Allowed.
     */
    @Test
    fun testMethodNotAllowed() {
        val (code, _) = httpRequest("/api/ints")
        assertEquals(405, code)
    }
}
