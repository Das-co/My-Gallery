package de.dasco.mygallery.timeline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.dasco.mygallery.R
import de.dasco.mygallery.databinding.HeaderItemBinding
import de.dasco.mygallery.databinding.RecyclerviewItemBinding
import de.dasco.mygallery.models.HeaderItem
import de.dasco.mygallery.models.MediaItem
import kotlinx.android.synthetic.main.header_item.view.*
import kotlinx.android.synthetic.main.recyclerview_item.view.*

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

class MyMediaItemRecyclerViewAdapter(
) : ListAdapter<DataItem, RecyclerView.ViewHolder>(DiffCallback) {

    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder(
                HeaderItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    )
                )
            )
            ITEM_VIEW_TYPE_ITEM -> ViewHolder(
                RecyclerviewItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    )
                )
            )
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val image = getItem(position) as DataItem.ImageItem
                tracker?.let {
                    holder.bind(image, it.isSelected(image.id))
                }
            }
            is HeaderViewHolder -> {
                val header = getItem(position) as DataItem.Header
                tracker?.let {
                    holder.bind(header, it.isSelected(header.id))
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.ImageItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

/*
    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        println("View r: ${holder.layoutPosition}")
        if (tracker?.hasSelection()!!) {
            holder.itemView.checkBoxImage?.visibility = View.VISIBLE
            holder.itemView.constraintLayout?.transitionToEnd()
        } else {
            holder.itemView.checkBoxImage?.visibility= View.INVISIBLE
            holder.itemView.constraintLayout?.transitionToStart()

        }

        super.onViewRecycled(holder)
    }
*/


    //Necessary to set the views in the desired state that are not on screen onSelectionChange, but are also not bound again
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {

        //State of the checkboxes
        if (tracker?.hasSelection()!!) {
            holder.itemView.checkBoxContainer?.transitionToEnd()
            holder.itemView.headerContainer?.transitionToEnd()
        } else {
            holder.itemView.checkBoxContainer?.transitionToStart()
            holder.itemView.headerContainer?.transitionToStart()
        }

        //state of the images
        if(tracker?.isSelected(holder.itemId)!!){
            holder.itemView.imageContainer?.setTransitionDuration(1)
            holder.itemView.imageContainer?.transitionToEnd()
            holder.itemView.imageContainer?.setTransitionDuration(100)
        }else{
            //shortening the transition duration so User don't see the transition when scrolling
            holder.itemView.imageContainer?.setTransitionDuration(1)
            holder.itemView.imageContainer?.transitionToStart()
            holder.itemView.imageContainer?.setTransitionDuration(100)
        }

        super.onViewAttachedToWindow(holder)
    }


    override fun getItemId(position: Int) = getItem(position).id

    inner class ViewHolder(private var binding: RecyclerviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataItem.ImageItem, selected: Boolean) {
            binding.image = item
            binding.textView.text = adapterPosition.toString()
            println("Binding: ${adapterPosition.toString()}")
            binding.checkBoxImage.isChecked = selected

            println("Has selection ${tracker!!.hasSelection()}")
            if (tracker!!.hasSelection()) {
                if (selected) {
                    binding.imageContainer.transitionToEnd()

                } else {
                    binding.imageContainer.transitionToStart()

                }
                binding.checkBoxContainer.transitionToEnd()
            } else {
                binding.imageContainer.transitionToStart()
                binding.checkBoxContainer.transitionToStart()
            }

            binding.executePendingBindings()
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long? = itemId
            }
    }

    inner class HeaderViewHolder(private var binding: HeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataItem.Header, selected: Boolean) {

            binding.checkBoxHeader.isChecked = selected



            if (tracker!!.hasSelection()) {
                if (selected) {
                    binding.headerContainer.transitionToEnd()
                }
            } else {
                binding.headerContainer.transitionToStart()
            }


            binding.setText(item)
            binding.executePendingBindings()
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long? = itemId
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