package de.dasco.mygallery

import android.os.Parcelable
import android.util.Log
import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import de.dasco.mygallery.timeline.MyMediaItemRecyclerViewAdapter


class MyItemDetailsLookup(var recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
//        Log.e("MyItemDetailsLookup", "$view")
        if (view != null) {
            if(recyclerView.getChildViewHolder(view) is MyMediaItemRecyclerViewAdapter.ViewHolder) {
                return (recyclerView.getChildViewHolder(view) as MyMediaItemRecyclerViewAdapter.ViewHolder)
                    .getItemDetails()
            }else if(recyclerView.getChildViewHolder(view) is MyMediaItemRecyclerViewAdapter.HeaderViewHolder)
                return (recyclerView.getChildViewHolder(view) as MyMediaItemRecyclerViewAdapter.HeaderViewHolder)
                    .getItemDetails()
        }
        return null
    }


}
