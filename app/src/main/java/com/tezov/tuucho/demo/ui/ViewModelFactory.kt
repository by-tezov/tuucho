package com.tezov.tuucho.demo.ui

class ViewModelFactory(private val useCase: GetStringUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StringViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StringViewModel(useCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}