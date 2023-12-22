package com.example.compose_recyclerview.adapter

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.example.compose_recyclerview.model.ComposeRecyclerViewItem


/**
 * RecyclerView adapter for handling different types of Compose items.
 */
internal class ComposeRecyclerViewAdapter : RecyclerView.Adapter<ComposeRecyclerViewAdapter.ComposeRecyclerViewHolder>() {

    var dataList: List<ComposeRecyclerViewItem> = emptyList()
        set(value) {
            if (field == value) return
            val index = this.dataList.indexOfFirst { it.hashCode() != field.hashCode() }
            field = value
            notifyItemChanged(index)
        }

    class ComposeRecyclerViewHolder(val composeView: ComposeView) :
        RecyclerView.ViewHolder(composeView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComposeRecyclerViewHolder {
        val context = parent.context
        val composeView = ComposeView(context)
        return ComposeRecyclerViewHolder(composeView)
    }

    override fun onBindViewHolder(holder: ComposeRecyclerViewHolder, position: Int) {
        val item = dataList[position]

        holder.composeView.setContent {
            when (item) {
                is ComposeRecyclerViewItem.ComposeDataItem<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val dataItem = item as ComposeRecyclerViewItem.ComposeDataItem<Any>
                    dataItem.content(dataItem.data)
                }
                is ComposeRecyclerViewItem.CustomComposableItem -> item.content()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return dataList[position].itemType
    }

    override fun getItemCount(): Int = dataList.size
}
