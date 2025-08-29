package ru.ettransapp.ui.navigation

sealed class Screen(val route: String) {
    object Login         : Screen("login")
    object MainMenu      : Screen("main_menu")
    object Checkout      : Screen("checkout/{carId}/{type}") {
        fun createRoute(carId: Int, type: String) = "checkout/$carId/$type"
    }
    object ActiveSession : Screen("active_session/{sessionId}") {
        fun createRoute(sessionId: Int) = "active_session/$sessionId"
    }
}