package de.dasco.mygallery.timeline

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import de.dasco.mygallery.R
import de.dasco.mygallery.databinding.HeaderItemBinding
import de.dasco.mygallery.databinding.RecyclerviewItemBinding
import de.dasco.mygallery.models.HeaderItem
import de.dasco.mygallery.models.MediaItem

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

class MyMediaItemRecyclerViewAdapter(
) : ListAdapter<DataItem, RecyclerView.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val image = getItem(position) as DataItem.ImageItem
                holder.bind(image)
            }
            is HeaderViewHolder -> {
                val header = getItem(position) as DataItem.Header
                holder.bind(header)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.ImageItem -> ITEM_VIEW_TYPE_ITEM
        }
    }


    class ViewHolder(private var binding: RecyclerviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataItem.ImageItem) {
            binding.image = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(RecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context)))
            }
        }
    }

    class HeaderViewHolder(private var binding: HeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataItem.Header) {
            binding.setText(item)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                return HeaderViewHolder(HeaderItemBinding.inflate(LayoutInflater.from(parent.context)))
            }
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }
    }
}

sealed class DataItem {
    data class ImageItem(var image: MediaItem) : DataItem() {
        override val id = image.id
    }

    data class Header(var header: HeaderItem) : DataItem() {
        override val id = header.id
    }

    abstract val id: Long
}