package de.dasco.mygallery.utils

import java.text.SimpleDateFormat
import java.util.*

fun smartDate(date: Long): String{
    val formatter = SimpleDateFormat("E, d MMMM yyyy", Locale.getDefault())
/*
    println("Formatted date: ${date}")
    println("Formatted date: ${formatter.format(date)}")
*/
    return formatter.format(date*1000)
}