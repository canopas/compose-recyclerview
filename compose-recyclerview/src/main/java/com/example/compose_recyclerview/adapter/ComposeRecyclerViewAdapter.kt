package com.example.compose_recyclerview.adapter

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.example.compose_recyclerview.data.LayoutOrientation

/**
 * RecyclerView adapter for handling dynamically generated Compose items.
 */
class ComposeRecyclerViewAdapter :
    RecyclerView.Adapter<ComposeRecyclerViewAdapter.ComposeRecyclerViewHolder>(){

    interface ItemTypeBuilder {
        fun getItemType(position: Int): Int
    }

    var totalItems: Int = 0
        set(value) {
            if (field == value) return
            field = value
            if (field == -1) {
                notifyItemInserted(0)
            } else {
                notifyItemChanged(0)
            }
        }

    var itemBuilder: (@Composable (index: Int) -> Unit)? =
        null

    var itemTypeBuilder: ItemTypeBuilder? = null

    var layoutOrientation: LayoutOrientation = LayoutOrientation.Vertical
        set(value) {
            if (field == value) return
            field = value
            notifyItemChanged(0)
        }

    inner class ComposeRecyclerViewHolder(val composeView: ComposeView) :
        RecyclerView.ViewHolder(composeView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComposeRecyclerViewHolder {
        val context = parent.context
        val composeView = ComposeView(context)
        return ComposeRecyclerViewHolder(composeView)
    }

    override fun onBindViewHolder(holder: ComposeRecyclerViewHolder, position: Int) {
        holder.composeView.apply {
            tag = holder
            setContent {
                itemBuilder?.invoke(position)
            }
        }
    }

    override fun getItemCount(): Int = totalItems

    override fun getItemViewType(position: Int): Int {
        return itemTypeBuilder?.getItemType(position) ?: 0
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
    }

    fun update(
        itemCount: Int,
        itemBuilder: @Composable (index: Int) -> Unit,
        layoutOrientation: LayoutOrientation,
        itemTypeBuilder: ItemTypeBuilder?
    ) {
        this.totalItems = itemCount
        this.itemBuilder = itemBuilder
        this.layoutOrientation = layoutOrientation
        itemTypeBuilder?.let {
            this.itemTypeBuilder = it
        }
    }
}