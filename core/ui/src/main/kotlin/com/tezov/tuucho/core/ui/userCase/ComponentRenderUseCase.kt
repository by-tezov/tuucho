package com.tezov.tuucho.core.ui.userCase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tezov.tuucho.core.domain.repository.MaterialRepository
import com.tezov.tuucho.core.ui.renderer.MaterialRenderer
import kotlinx.serialization.json.JsonObject

class ComponentRenderUseCase(
    private val repository: MaterialRepository,
    private val renderer: MaterialRenderer,
) {

    @Composable
    operator fun invoke(url: String, loadingView: @Composable () -> Unit) {
        var component by remember { mutableStateOf<JsonObject?>(null) }
        LaunchedEffect(url) { component = repository.retrieve(url) }
        component?.let {
            val render by remember {
                mutableStateOf(renderer.process(it))
            }
            render?.apply { show() }
        } ?: run {
            loadingView()
        }
    }

}