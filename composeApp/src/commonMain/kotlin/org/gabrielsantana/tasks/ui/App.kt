package org.gabrielsantana.tasks.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import org.gabrielsantana.tasks.features.create.ui.CreateTaskScreen
import org.gabrielsantana.tasks.features.home.ui.HomeScreen
import org.gabrielsantana.tasks.features.login.ui.LoginScreen
import org.gabrielsantana.tasks.features.settings.SettingsScreen
import org.gabrielsantana.tasks.features.settings.appearance.ui.AppearanceScreen
import org.gabrielsantana.tasks.ui.theme.DarkColorScheme
import org.gabrielsantana.tasks.ui.theme.LightColorScheme
import org.gabrielsantana.tasks.ui.theme.Typography
import org.koin.compose.getKoin

enum class RootScreens(val title: String) {
    Login("Login"),
    Home("Home"),
    CreateTask("Create task"),
    Settings("Settings"),
    Appearance("Apperance");
}


fun interface ColorSchemeProvider {
    @Composable
    fun provide(isDarkTheme: Boolean): ColorScheme

    companion object {
        val DEFAULT = ColorSchemeProvider { isDarkTheme ->
            if (isDarkTheme) DarkColorScheme else LightColorScheme
        }
    }
}


@Composable
fun App(
    navController: NavHostController = rememberNavController(),
    appState: AppState = rememberAppState(getKoin().get()),
) {
    val darkTheme = appState.isDarkMode
    val isAmoled by appState.isAmoled
    val colorSeed by appState.seedColor
    val isDynamicColorEnabled by appState.isDynamicColorEnabled

    val isLoggedIn by appState.isLoggedIn.collectAsStateWithLifecycle()
    val startDestination = if (isLoggedIn) RootScreens.Home.name else RootScreens.Login.name


    TasksTheme(
        dynamicColorSeed = colorSeed,
        darkTheme = darkTheme,
        isDynamicColorEnabled = isDynamicColorEnabled,
        isAmoled = isAmoled,
    ) {
        val graph = navController.createGraph(startDestination = startDestination) {
            composable(RootScreens.Login.name) {
                LoginScreen(
                    onNavigateToHome = {
//                        navController.navigate(RootScreens.Home.name) {
//                            popUpTo(RootScreens.Login.name) {
//                                inclusive = true
//                            }
//                        }
                    }
                )
            }
            composable(RootScreens.Home.name) { entry ->
                val taskCreated =
                    entry.savedStateHandle.get<Boolean>("taskCreatedSuccessfully") == true
                HomeScreen(
                    onNavigateToCreateTask = {
                        navController.navigate(RootScreens.CreateTask.name)
                    },
                    taskCreated = taskCreated,
                    onTaskCreated = {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "taskCreatedSuccessfully",
                            false
                        )
                    },
                    onNavigateToSettings = {
                        navController.navigate(RootScreens.Settings.name)
                    }
                )
            }
            composable(RootScreens.CreateTask.name) {
                CreateTaskScreen(
                    onNavigateBack = {
                        if (navController.currentBackStackEntry?.destination?.route == RootScreens.CreateTask.name) {
                            navController.popBackStack()
                            if (it) navController.currentBackStackEntry?.savedStateHandle?.set(
                                "taskCreatedSuccessfully",
                                true
                            )
                        }
                    },
                )
            }
            composable(RootScreens.Settings.name) {
                SettingsScreen(
                    onNavigateToAppearance = {
                        navController.navigate(RootScreens.Appearance.name)
                    },
                    onNavigateBack = {
                        if (navController.currentBackStackEntry?.destination?.route == RootScreens.Settings.name) {
                            navController.popBackStack()
                        }
                    }
                )
            }
            composable(RootScreens.Appearance.name) {
                AppearanceScreen(
                    onNavigateBack = {
                        if (navController.currentBackStackEntry?.destination?.route == RootScreens.Appearance.name) {
                            navController.popBackStack()
                        }
                    }
                )
            }
        }
        NavHost(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    tween(400, easing = FastOutSlowInEasing)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    tween(400, easing = FastOutSlowInEasing)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    tween(400, easing = FastOutSlowInEasing)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    tween(400, easing = FastOutSlowInEasing)
                )
            },
            navController = navController,
            graph = graph
        )
    }
}
