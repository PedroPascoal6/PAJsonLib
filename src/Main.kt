import framework.App
import controllers.Controller

fun main() {
    App(Controller::class).start(8080)
}
