package de.dasco.mygallery.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class MediaItem(
    var id: Long,
    var uri: String,
    var size: Long,
    var date: Long
)
