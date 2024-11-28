@file:OptIn(ExperimentalMaterial3Api::class)

package org.gabrielsantana.tasks

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import org.gabrielsantana.tasks.features.create.ui.CreateTaskScreen
import org.gabrielsantana.tasks.features.home.ui.HomeScreen
import org.gabrielsantana.tasks.features.settings.SettingsScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

enum class RootScreens(val title: String) {
    Home("Home"),
    CreateTask("Create task"),
    Settings("Settings");
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(
    navController: NavHostController = rememberNavController(),
    appState: AppState = rememberAppState()
) {
    TasksTheme(
        darkTheme = appState.isDarkMode,
        dynamicColor = appState.isDynamicColor
    ) {
        val graph = navController.createGraph(startDestination = RootScreens.Home.name) {
            composable(RootScreens.Home.name) { entry ->
                val taskCreated = entry.savedStateHandle.get<Boolean>("taskCreatedSuccessfully") == true
                HomeScreen(
                    onNavigateToCreateTask = {
                        navController.navigate(RootScreens.CreateTask.name)
                    },
                    taskCreated = taskCreated,
                    onTaskCreated = {
                        navController.currentBackStackEntry?.savedStateHandle?.set("taskCreatedSuccessfully", false)
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
                            if (it) navController.currentBackStackEntry?.savedStateHandle?.set("taskCreatedSuccessfully", true)
                        }
                    },
                )
            }
            dialog(RootScreens.Settings.name) {
                SettingsScreen(
                    themeMode = appState.themeMode,
                    isDynamicColorsEnabled = appState.isDynamicColor,
                    onChangeThemeMode = { themeMode ->
                        appState.themeMode = themeMode
                    },
                    onDismissRequest = {
                        navController.popBackStack()
                    },
                    onToggleDynamicColors = { isDynamicColors ->
                        appState.isDynamicColor = isDynamicColors
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
                    tween(400, easing = FastOutSlowInEasing))
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
