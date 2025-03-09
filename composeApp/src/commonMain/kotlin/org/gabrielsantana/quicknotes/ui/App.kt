package org.gabrielsantana.quicknotes.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gabrielsantana.quicknotes.feature.home.ui.HomeScreen
import org.gabrielsantana.quicknotes.feature.preferences.preferencesNavigation
import org.gabrielsantana.quicknotes.features.create.ui.CreateTaskScreen
import org.gabrielsantana.quicknotes.features.create.ui.TaskAction
import org.gabrielsantana.quicknotes.features.login.ui.LoginScreen
import org.gabrielsantana.quicknotes.ui.theme.TasksTheme
import org.koin.compose.getKoin

@Composable
fun App(
    navController: NavHostController = rememberNavController(),
    appState: AppState = rememberAppState(getKoin().get()),
) {
    val darkTheme = appState.themeMode.collectAsStateWithLifecycle().value.isDarkMode
    val isAmoled by appState.isAmoled.collectAsStateWithLifecycle()
    val colorSeed by appState.seedColor.collectAsStateWithLifecycle()
    val isDynamicColorEnabled by appState.isDynamicColorEnabled.collectAsStateWithLifecycle()
    val isLoggedIn by appState.isLoggedIn.collectAsStateWithLifecycle()
    val startDestination = if (isLoggedIn) AppScreens.Home else AppScreens.Login

    TasksTheme(
        dynamicColorSeed = colorSeed,
        darkTheme = darkTheme,
        isDynamicColorEnabled = isDynamicColorEnabled,
        isAmoled = isAmoled,
    ) {
        val graph = navController.createGraph(startDestination = startDestination) {
            composable<AppScreens.Login> {
                LoginScreen()
            }
            composable<AppScreens.Home> { entry ->
                val taskActionDone = entry.savedStateHandle.get<String>("taskAction")?.let {
                    Json.decodeFromString<TaskAction>(it)
                }
                val snackbarHostState = remember { SnackbarHostState() }
                LaunchedEffect(taskActionDone) {
                    if (taskActionDone != null) {
                        when (taskActionDone) {
                            TaskAction.Create -> snackbarHostState.showSnackbar("Task created")
                            is TaskAction.Update -> snackbarHostState.showSnackbar("Task updated")
                        }
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "taskAction",
                            null
                        )
                    }
                }
                HomeScreen(
                    onNavigateToCreateTask = {
                        navController.navigate(AppScreens.CreateTask())
                    },
                    onNavigateToSettings = {
                        navController.navigate(AppScreens.Preferences)
                    },
                    onNavigateToEditTask = {
                        navController.navigate(AppScreens.CreateTask(it))
                    },
                    snackbarHostState = snackbarHostState
                )
            }
            composable<AppScreens.CreateTask> {
                CreateTaskScreen(
                    onNavigateBack = { action: TaskAction? ->
                        navController.popBackStack()
                        if (action != null) navController.currentBackStackEntry?.savedStateHandle?.set(
                            "taskAction",
                            Json.encodeToString(action)
                        )
                    },
                )
            }
            preferencesNavigation<AppScreens.Preferences>(navController)
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
