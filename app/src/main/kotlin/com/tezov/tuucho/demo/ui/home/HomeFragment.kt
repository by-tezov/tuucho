package com.tezov.tuucho.demo.ui.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val text by viewModel.text.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchStringData()
    }

    Text(text)
}