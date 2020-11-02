package de.dasco.mygallery.utils

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import de.dasco.mygallery.R
import de.dasco.mygallery.models.MediaItem
import de.dasco.mygallery.timeline.DataItem
import de.dasco.mygallery.timeline.MyMediaItemRecyclerViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val adapterScope = CoroutineScope(Dispatchers.Default)

@BindingAdapter("listDataTimeline")
fun bindGalleryRecyclerView(recyclerView: RecyclerView, data: List<DataItem>?) {
//    println("binding adapter ${data}")
    val adappter = recyclerView.adapter as MyMediaItemRecyclerViewAdapter
    adappter.submitList(data)

}

/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUri: String) {
    imgUri.let {
        GlideApp.with(imgView.context)
            .load(Uri.parse(imgUri))
            .override(256)
            .thumbnail(0.1f)
            .centerCrop()
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
//                .error(R.drawable.ic_broken_image)
            )
            .into(imgView)
    }
}
/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */
@BindingAdapter("headerDate")
fun bindHeader(textView: TextView, date: String) {
//    println("Bind header: ")
//    println("Bind header: $date")
    date.let {
        textView.text = date
    }
}
