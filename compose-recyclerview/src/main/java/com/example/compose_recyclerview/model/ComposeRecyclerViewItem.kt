package com.example.compose_recyclerview.model

import androidx.compose.runtime.Composable


/**
 * Represents a **Compose** item in the **RecyclerView**.
 */
sealed class ComposeRecyclerViewItem {

    abstract val itemType: Int

    /**
     * Represents a data item with associated Composable content.
     * @param data The data to display.
     * @param content The Composable function to define UI for the data.
     */
    data class ComposeDataItem<T>(val data: T, val content: @Composable (T) -> Unit) : ComposeRecyclerViewItem() {
        override val itemType: Int = 0
    }

    /**
     * Represents a custom Composable item.
     * @param content The Composable function to define UI.
     */
    data class CustomComposableItem(val content: @Composable () -> Unit) : ComposeRecyclerViewItem() {
        override val itemType: Int = 1
    }
}
