package com.tezov.tuucho.demo.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel(private val getPageObject: GetPageObjectUseCase) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun fetchStringData(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val result = getPageObject("page://home")
            onResult(result.message)
        }
    }
}