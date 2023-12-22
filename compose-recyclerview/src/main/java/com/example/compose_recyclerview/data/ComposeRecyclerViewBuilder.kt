package com.example.compose_recyclerview.data

import androidx.compose.runtime.Composable
import com.example.compose_recyclerview.model.ComposeRecyclerViewItem


/**
 * Builder class to simplify the creation of a list of Compose items for RecyclerView.
 */
class ComposeRecyclerViewBuilder {
    private val items = mutableListOf<ComposeRecyclerViewItem>()

    /**
     * Adds a custom Composable item to the list.
     * @param content The Composable function to define UI.
     */
    fun addCustomComposable(content: @Composable () -> Unit) {
        items.add(ComposeRecyclerViewItem.CustomComposableItem(content))
    }

    /**
     * Adds a list of data items with associated Composable content to the list.
     * @param data The list of data items.
     * @param content The Composable function to define UI for each data item.
     */
    fun <T> addDataItem(data: List<T>, content: @Composable (T) -> Unit) {
        data.forEach {
            items.add(ComposeRecyclerViewItem.ComposeDataItem(it, content))
        }
    }

    /**
     * Builds and returns the list of Compose items.
     */
    fun build(): List<ComposeRecyclerViewItem> {
        return items.toList()
    }
}
