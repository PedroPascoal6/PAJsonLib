import framework.GetJson
import controllers.Controller

fun main() {
    GetJson(Controller::class).start(8080)
}
