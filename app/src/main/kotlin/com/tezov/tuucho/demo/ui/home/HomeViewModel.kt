package com.tezov.tuucho.demo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tezov.tuucho.core.data.cache.database.Database
import com.tezov.tuucho.core.domain.usecase.GetMaterialUseCase
import com.tezov.tuucho.core.domain.usecase.RefreshCacheMaterialUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val database: Database,
    private val refreshCacheMaterials: RefreshCacheMaterialUseCase,
    private val getMaterials: GetMaterialUseCase
) : ViewModel() {

    private val _text = MutableStateFlow("This is home Fragment")
    val text = _text.asStateFlow()

    fun fetchStringData() {
        viewModelScope.launch {
            delay(500)

            refreshCacheMaterials("config")
            getMaterials("page-home")
        }
    }

}