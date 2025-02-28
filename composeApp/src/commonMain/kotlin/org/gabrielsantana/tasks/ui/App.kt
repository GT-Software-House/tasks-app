package org.gabrielsantana.tasks.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gabrielsantana.tasks.data.TasksRepository
import org.gabrielsantana.tasks.features.create.ui.CreateTaskScreen
import org.gabrielsantana.tasks.features.create.ui.TaskAction
import org.gabrielsantana.tasks.features.home.ui.HomeScreen
import org.gabrielsantana.tasks.features.login.ui.LoginScreen
import org.gabrielsantana.tasks.features.settings.SettingsScreen
import org.gabrielsantana.tasks.features.settings.appearance.ui.AppearanceScreen
import org.gabrielsantana.tasks.ui.theme.TasksTheme
import org.koin.compose.getKoin
import org.koin.compose.koinInject

@Composable
fun App(
    navController: NavHostController = rememberNavController(),
    appState: AppState = rememberAppState(getKoin().get()),
    tasksRepository: TasksRepository = koinInject()
) {
    val darkTheme = appState.themeMode.collectAsStateWithLifecycle().value.isDarkMode
    val isAmoled by appState.isAmoled.collectAsStateWithLifecycle()
    val colorSeed by appState.seedColor.collectAsStateWithLifecycle()
    val isDynamicColorEnabled by appState.isDynamicColorEnabled.collectAsStateWithLifecycle()
    val isLoggedIn by appState.isLoggedIn.collectAsStateWithLifecycle()
    val startDestination = if (isLoggedIn) AppScreens.Home else AppScreens.Login

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            tasksRepository.reactiveSync()
        }
    }

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
                        navController.navigate(AppScreens.Settings)
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
            composable<AppScreens.Settings> {
                SettingsScreen(
                    onNavigateToAppearance = {
                        navController.navigate(AppScreens.Appearance)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable<AppScreens.Appearance> {
                AppearanceScreen(
                    onNavigateBack = {
                        navController.popBackStack()
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
