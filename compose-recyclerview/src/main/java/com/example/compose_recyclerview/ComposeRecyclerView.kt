package com.example.compose_recyclerview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compose_recyclerview.adapter.ComposeRecyclerViewAdapter
import com.example.compose_recyclerview.data.LayoutOrientation
import com.example.compose_recyclerview.model.ComposeRecyclerViewItem

/**
 * Composable function to display a RecyclerView with a list of Compose items.
 * @param modifier The modifier to be applied to the RecyclerView.
 * @param items The list of Compose items to display.
 * @param orientation The layout direction of the RecyclerView.
 */
@Composable
fun ComposeRecyclerView(
    modifier: Modifier = Modifier,
    items: List<ComposeRecyclerViewItem>,
    orientation: LayoutOrientation = LayoutOrientation.Vertical
) {
    val context = LocalContext.current

    val adapter = remember {
        ComposeRecyclerViewAdapter().apply {
            this.dataList = items
        }
    }

    val recyclerView = remember {
        RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context).apply {
                this.orientation = when (orientation) {
                    LayoutOrientation.Horizontal -> RecyclerView.HORIZONTAL
                    LayoutOrientation.Vertical -> RecyclerView.VERTICAL
                }
            }
            this.adapter = adapter
        }
    }

    // Use AndroidView to embed the RecyclerView in the Compose UI
    AndroidView(
        factory = { recyclerView },
        modifier = modifier,
        update = { _ ->
            // TODO - Update the RecyclerView if needed
        }
    )
}