@file:OptIn(ExperimentalMaterial3Api::class)

package org.gabrielsantana.tasks.features.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mmk.kmpauth.google.GoogleButtonUiContainer
import com.mmk.kmpauth.google.GoogleUser
import org.gabrielsantana.tasks.features.login.ui.icons.filled.Google
import org.jetbrains.compose.ui.tooling.preview.Preview

private const val TAG = "LoginScreen.kt"

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToHome: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.isSignedIn) {
        if (uiState.isSignedIn) {
            onNavigateToHome()
        }
    }
    LoginContent(
        uiState = uiState,
        onSignResult = viewModel::signInWithGoogleUser
    )
}


@Composable
fun LoginContent(
    uiState: LoginUiState,
    onSignResult: (GoogleUser?) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
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
            GoogleButtonUiContainer(onGoogleSignInResult = onSignResult) {
                Button(
                    onClick = this::onClick
                ) {
                    Icon(Icons.Default.Google, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enter with Google")
                }
            }
            if (uiState.isError) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Something went wrong",
                    color = MaterialTheme.colorScheme.error
                )
            }
            if (uiState.isLoading) {
                Dialog(onDismissRequest = {}) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Preview
@Composable
fun LoginContentPreview(modifier: Modifier = Modifier) {
    MaterialTheme {
        LoginContent(
            onSignResult = {},
            uiState = LoginUiState(isSignedIn = true),
            modifier = modifier
        )
    }
}