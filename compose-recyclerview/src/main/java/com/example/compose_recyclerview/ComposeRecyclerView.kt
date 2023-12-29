package com.example.compose_recyclerview

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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compose_recyclerview.adapter.ComposeRecyclerViewAdapter
import com.example.compose_recyclerview.data.LayoutOrientation
import com.example.compose_recyclerview.utils.InfiniteScrollListener

/**
 * Composable function to display a RecyclerView with dynamically generated Compose items.
 *
 * @param modifier The modifier to be applied to the RecyclerView.
 * @param itemCount The total number of items to be displayed in the RecyclerView.
 * @param itemBuilder The lambda function responsible for creating the Compose content for each item at the specified index.
 * @param onScrollEnd Callback triggered when the user reaches the end of the list during scrolling.
 * @param orientation The layout direction of the RecyclerView.
 * @param itemTypeBuilder The optional lambda function to determine the type of each item.
 *  * Required for effective drag and drop. Provide a non-null [ComposeRecyclerViewAdapter.ItemTypeBuilder] when enabling drag and drop functionality.
 *  * Useful when dealing with multiple item types, ensuring proper handling and layout customization for each type.
 * @param onDragCompleted Callback triggered when an item drag operation is completed.
 * @param onItemMove Callback triggered when an item is moved within the RecyclerView.
 * @param onCreate Callback to customize the RecyclerView after its creation.
 */
@Composable
fun ComposeRecyclerView(
    modifier: Modifier = Modifier,
    itemCount: Int,
    itemBuilder: @Composable (index: Int) -> Unit,
    onScrollEnd: () -> Unit = {},
    orientation: LayoutOrientation = LayoutOrientation.Vertical,
    itemTypeBuilder: ComposeRecyclerViewAdapter.ItemTypeBuilder? = null,
    onDragCompleted: (position: Int) -> Unit = { _ -> },
    onItemMove: (fromPosition: Int, toPosition: Int, itemType: Int) -> Unit = { _, _, _ -> },
    onCreate: (RecyclerView) -> Unit = {}
) {
    val context = LocalContext.current
    var scrollState by rememberSaveable { mutableStateOf(bundleOf()) }

    val layoutManager = remember {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.onRestoreInstanceState(scrollState.getParcelable("RecyclerviewState"))
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
            itemTypeBuilder?.let {
                this.itemTypeBuilder = itemTypeBuilder
            }
            this.layoutOrientation = orientation
        }
    }

    val composeRecyclerView = remember {
        RecyclerView(context).apply {
            this.layoutManager = layoutManager
            addOnScrollListener(object : InfiniteScrollListener() {
                override fun onScrollEnd() {
                    onScrollEnd()
                }
            })
            this.adapter = adapter
        }
    }

    val itemTouchHelper = remember {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(UP or DOWN or START or END, 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromType = adapter.getItemViewType(viewHolder.bindingAdapterPosition)
                val toType = adapter.getItemViewType(target.bindingAdapterPosition)

                if (fromType != toType) {
                    return false
                }

                adapter.onItemMove(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
                onItemMove.invoke(
                    viewHolder.bindingAdapterPosition,
                    target.bindingAdapterPosition,
                    fromType
                )
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {  }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                viewHolder.itemView.alpha = 1f
                onDragCompleted.invoke(viewHolder.bindingAdapterPosition)
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.5f
                }
            }

            override fun isLongPressDragEnabled(): Boolean {
                return true
            }
        })
    }

    // Use AndroidView to embed the RecyclerView in the Compose UI
    AndroidView(
        factory = {
            composeRecyclerView.apply {
                onCreate.invoke(this)
                itemTypeBuilder?.let {
                    itemTouchHelper.attachToRecyclerView(this)
                }
            }
        },
        modifier = modifier,
        update = {
            adapter.update(itemCount, itemBuilder, orientation, itemTypeBuilder)
        }
    )

    DisposableEffect(key1 = Unit, effect = {
        onDispose {
            scrollState = bundleOf("RecyclerviewState" to layoutManager.onSaveInstanceState())
        }
    })
}