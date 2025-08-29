package ru.ettransapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import ru.ettransapp.ui.login.LoginScreen
import ru.ettransapp.ui.login.LoginViewModel
import ru.ettransapp.ui.screen.CheckoutScreen
import ru.ettransapp.ui.screen.ActiveSessionScreen
import ru.ettransapp.ui.screen.MainMenuScreen
import ru.ettransapp.ui.viewmodel.CarsViewModel
import ru.ettransapp.ui.theme.ETAppTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.LaunchedEffect
import ru.ettransapp.ui.viewmodel.CheckoutViewModel
import ru.ettransapp.ui.viewmodel.ActiveSessionViewModel

private object NavRoutes {
    const val Login = "login"
    const val MainMenu = "main_menu"
    const val Checkout = "checkout/{carId}"
    fun checkout(carId: Int) = "checkout/$carId"
    const val ActiveSession = "active_session"
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ETAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = NavRoutes.Login,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(NavRoutes.Login) {
                        val loginVm: LoginViewModel = hiltViewModel()
                        LoginScreen(
                            viewModel = loginVm,
                            onLoginSuccess = {
                                navController.navigate(NavRoutes.MainMenu) {
                                    popUpTo(NavRoutes.Login) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable(NavRoutes.MainMenu) {
                        val carsVm: CarsViewModel = hiltViewModel()
                        MainMenuScreen(
                            viewModel = carsVm,
                            onSelectCar = { car ->
                                navController.navigate(NavRoutes.checkout(car.id))
                            },
                            onShowTransfers = { /* TODO */ },
                            onShowSupport = { /* TODO */ }
                        )
                    }
                    composable(
                        route = NavRoutes.Checkout,
                        arguments = listOf(navArgument("carId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val carId = backStackEntry.arguments!!.getInt("carId")
                        val checkoutVm: CheckoutViewModel = hiltViewModel(backStackEntry)
                        CheckoutScreen(
                            carId = carId,
                            viewModel = checkoutVm,
                            onDone = {
                                navController.navigate(NavRoutes.ActiveSession)
                            }
                        )
                    }

                    composable(NavRoutes.ActiveSession) {
                        val activeVm: ActiveSessionViewModel = hiltViewModel()
                        ActiveSessionScreen(
                            viewModel = activeVm,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    ETAppTheme {
        // Preview UI does not include navigation
        Greeting("Preview")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    // Simple fallback screen
    Text(text = "Hello $name!", modifier = modifier.padding(16.dp))
}