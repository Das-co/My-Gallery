package de.dasco.mygallery

import androidx.recyclerview.selection.ItemKeyProvider
import de.dasco.mygallery.timeline.MyMediaItemRecyclerViewAdapter

class MyItemKeyProvider(private val adapter: MyMediaItemRecyclerViewAdapter) : ItemKeyProvider<Long>(SCOPE_CACHED)
{
    override fun getKey(position: Int): Long? =
        adapter.currentList[position].id
    override fun getPosition(key: Long): Int =
        adapter.currentList.indexOfFirst {it.id == key}
}