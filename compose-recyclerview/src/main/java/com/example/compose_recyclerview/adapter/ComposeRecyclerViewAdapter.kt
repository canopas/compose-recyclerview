package com.example.compose_recyclerview.adapter

import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.example.compose_recyclerview.data.LayoutOrientation

/**
 * RecyclerView adapter for handling dynamically generated Compose items.
 */
internal class ComposeRecyclerViewAdapter :
    RecyclerView.Adapter<ComposeRecyclerViewAdapter.ComposeRecyclerViewHolder>() {

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

    var itemBuilder: (@Composable (index: Int) -> Unit)? = null

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
        holder.composeView.setContent {
            when (layoutOrientation) {
                LayoutOrientation.Horizontal -> {
                    Row {
                        itemBuilder?.invoke(position)
                    }
                }

                LayoutOrientation.Vertical -> {
                    Column {
                        itemBuilder?.invoke(position)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = totalItems

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
