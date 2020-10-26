package de.dasco.mygallery.utils

import java.text.SimpleDateFormat
import java.util.*

fun Long.smartDate(date: Long): String{
    val formatter = SimpleDateFormat("E, d MMMM yyyy", Locale.getDefault())
    return formatter.format(date * 1000)
}