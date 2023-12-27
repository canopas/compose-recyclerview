package com.example.compose_recyclerview

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compose_recyclerview.adapter.ComposeRecyclerViewAdapter
import com.example.compose_recyclerview.data.LayoutOrientation


/**
 * Composable function to display a RecyclerView with dynamically generated Compose items.
 *
 * @param modifier The modifier to be applied to the RecyclerView.
 * @param itemCount The total number of items to be displayed in the RecyclerView.
 * @param itemBuilder The lambda function responsible for creating the Compose content for each item at the specified index.
 * @param onScrollEnd Callback triggered when the user reaches the end of the list during scrolling.
 * @param orientation The layout direction of the RecyclerView.
 */
@Composable
fun ComposeRecyclerView(
    modifier: Modifier = Modifier,
    itemCount: Int,
    itemBuilder: @Composable (index: Int) -> Unit,
    onScrollEnd: () -> Unit = {},
    orientation: LayoutOrientation = LayoutOrientation.Vertical,
    onCreate: (RecyclerView) -> Unit? = {}
) {
    val context = LocalContext.current
    var scrollState by rememberSaveable { mutableStateOf(bundleOf()) }
    val layoutManager = remember {
        val layoutManager = LinearLayoutManager(context)
        val parcelableState = scrollState.getParcelable<Parcelable?>("RecyclerviewState")
        layoutManager.onRestoreInstanceState(parcelableState)
        layoutManager.orientation = when (orientation) {
            LayoutOrientation.Horizontal -> RecyclerView.HORIZONTAL
            LayoutOrientation.Vertical -> RecyclerView.VERTICAL
        }
        layoutManager
    }

    val adapter = remember {
        ComposeRecyclerViewAdapter().apply {
            this.totalItems = itemCount
            this.itemBuilder = itemBuilder
            this.layoutOrientation = orientation
        }
    }

    val composeRecyclerView = remember {
        RecyclerView(context).apply {
            onCreate.invoke(this)
            this.layoutManager = layoutManager
            addOnScrollListener(object : InfiniteScrollListener() {
                override fun onScrollEnd() {
                    onScrollEnd()
                }
            })
            this.adapter = adapter
        }
    }

    // Use AndroidView to embed the RecyclerView in the Compose UI
    AndroidView(
        factory = { composeRecyclerView },
        modifier = modifier,
        update = {
            adapter.totalItems = itemCount
            adapter.itemBuilder = itemBuilder
            adapter.layoutOrientation = orientation
        }
    )


    DisposableEffect(key1 = Unit, effect = {
        onDispose {
            scrollState = bundleOf("RecyclerviewState" to layoutManager.onSaveInstanceState())
        }
    })
}

/**
 * Abstract class for handling infinite scrolling events in a RecyclerView.
 */
abstract class InfiniteScrollListener : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0 || dx > 0) {
            val layoutManager = recyclerView.layoutManager
            if (layoutManager is LinearLayoutManager) {
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()
                if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                    onScrollEnd()
                }
            }
        }
    }

    /**
     * Callback triggered when the user reaches the end of the list during scrolling.
     */
    protected abstract fun onScrollEnd()
}
