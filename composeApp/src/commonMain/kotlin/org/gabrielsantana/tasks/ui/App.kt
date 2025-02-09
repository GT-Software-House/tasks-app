@file:OptIn(ExperimentalMaterial3Api::class)

package org.gabrielsantana.tasks.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import org.gabrielsantana.tasks.features.settings.SettingsScreen
import org.gabrielsantana.tasks.features.settings.appearance.ui.AppearanceScreen
import org.gabrielsantana.tasks.ui.theme.DarkColorScheme
import org.gabrielsantana.tasks.ui.theme.LightColorScheme
import org.gabrielsantana.tasks.ui.theme.TasksTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.getKoin

enum class RootScreens(val title: String) {
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
@Preview
fun App(
    navController: NavHostController = rememberNavController(),
    appState: AppState = rememberAppState(getKoin().get())
) {
    val darkTheme = appState.isDarkMode
    val isAmoled by appState.isAmoled
    val colorSeed by appState.seedColor
    val isDynamicColorEnabled by appState.isDynamicColorEnabled

    TasksTheme(
        dynamicColorSeed = colorSeed,
        darkTheme = darkTheme,
        isDynamicColorEnabled = isDynamicColorEnabled,
        isAmoled = isAmoled,
    ) {
        val graph = navController.createGraph(startDestination = RootScreens.Home.name) {
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
