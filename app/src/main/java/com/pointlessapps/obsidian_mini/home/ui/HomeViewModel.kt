package com.pointlessapps.obsidian_mini.home.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel

internal data class HomeState(
    val textValue: TextFieldValue = TextFieldValue(),
)

internal class HomeViewModel(
//    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var state by mutableStateOf(HomeState())
        private set

    fun onTextValueChanged(value: TextFieldValue) {
        state = state.copy(textValue = value)
    }
}
