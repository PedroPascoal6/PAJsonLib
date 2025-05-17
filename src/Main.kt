// src/main/kotlin/Main.kt
import framework.GetJson
import controllers.Controller

fun main() {
    // injeta seu controller e dispara na  porta 8080
    GetJson(Controller::class).start(8080)
}
