@file:OptIn(ExperimentalMaterial3Api::class)

package org.gabrielsantana.quicknotes.feature.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.composable.GoogleDialogType
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.NativeSignInStatus
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import kotlinx.coroutines.launch
import org.gabrielsantana.quicknotes.core.components.HorizontalSpacer
import org.gabrielsantana.quicknotes.feature.auth.ui.filled.Google
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
fun LoginScreen(
    supabaseClient: SupabaseClient = koinInject(),
    onLoginSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val authState = supabaseClient.composeAuth.rememberSignInWithGoogle(
        type = GoogleDialogType.BOTTOM_SHEET,
        onResult = {
            when (it) {
                NativeSignInResult.ClosedByUser -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Sign in cancelled",
                            withDismissAction = true
                        )
                    }
                }
                is NativeSignInResult.Error -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "An error occurred. Try again.",
                            withDismissAction = true
                        )
                    }
                }

                is NativeSignInResult.NetworkError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "An error occurred with the network. Try Again.",
                            withDismissAction = true
                        )
                    }
                }

                NativeSignInResult.Success -> onLoginSuccess
            }

        },
        fallback = {
            snackbarHostState.showSnackbar(
                message = "Your device is not supported. Contact the developers.",
                withDismissAction = true
            )
        }
    )
    if (authState.status is NativeSignInStatus.Started) {
        Dialog(onDismissRequest = {}) {
            CircularProgressIndicator()
        }
    }
    LoginContent(
        onSignInClick = {
            authState.startFlow()
        },
        snackbarHostState = snackbarHostState
    )
}

@Composable
internal fun LoginContent(
    onSignInClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            LargeTopAppBar(title = { Text("Hey there. Welcome to SupperApp.") })
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onSignInClick
            ) {
                Icon(imageVector = Icons.Default.Google, contentDescription = null)
                HorizontalSpacer(width = 8.dp)
                Text(text = "Enter with Google")
            }
        }
    }
}

@Preview
@Composable
private fun LoginContentPreview() {
    MaterialTheme {
        LoginContent(
            onSignInClick = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}