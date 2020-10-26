package de.dasco.mygallery.models

import de.dasco.mygallery.utils.smartDate

data class HeaderItem(
    val id: Long,
    val date: Long
) {
    val formattedDate: String
        get() = date.smartDate(date)
}