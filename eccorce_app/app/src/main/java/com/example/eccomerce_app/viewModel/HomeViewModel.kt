package com.example.eccomerce_app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel(){
    val accessHomeScreenCounter=MutableStateFlow<Int>(0)

    fun increaseAccessHomeScreenCounter(){
        viewModelScope.launch(Dispatchers.IO) {
          val result =    accessHomeScreenCounter.value++
            accessHomeScreenCounter.emit(result);

        }
    }
}