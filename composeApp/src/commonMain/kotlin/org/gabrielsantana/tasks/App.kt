@file:OptIn(ExperimentalMaterial3Api::class)

package org.gabrielsantana.tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.koinApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(
    viewModel: AppViewModel = koinViewModel()
) {
    val name by viewModel.name.collectAsStateWithLifecycle()
    KoinContext {
        MaterialTheme {
            Scaffold(
              topBar = {
                  TopAppBar(
                      title = { Text(name) }
                  )
              }
            ) {
                Column(
                    modifier = Modifier.padding(it)
                ) {

                }
            }
        }

    }
}

@Composable
fun TaskItem(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(text = title)
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}
