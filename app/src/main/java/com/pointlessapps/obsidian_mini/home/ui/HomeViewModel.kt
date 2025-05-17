package com.pointlessapps.obsidian_mini.home.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel

internal data class HomeState(
    val nodes: List<Node> = listOf(ParagraphNode(TextFieldValue())),
)

internal class HomeViewModel(
//    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var state by mutableStateOf(HomeState())
        private set

    fun onNodeValueChanged(node: ParagraphNode, value: TextFieldValue) {
        state = state.copy(
            nodes = state.nodes.map {
                if (it == node) {
                    node.copy(value = value)
                } else {
                    it
                }
            }
        )
    }

    fun onInsertNodeStyle() {
    }
}
