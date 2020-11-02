package de.dasco.mygallery.models

import de.dasco.mygallery.utils.smartDate

data class HeaderItem(
    val id: Long,
    val date: Long,
    var children: ArrayList<Long>
) {
    val formattedDate: String
        get() = smartDate(date)
}