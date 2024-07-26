package com.supremebeing.phoneanti.aichat.utils

import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.tool.toastShort
import java.util.Calendar

val monthList = mutableListOf<String>().apply {
    add("January")
    add("February")
    add("March")
    add("April")
    add("May")
    add("June")
    add("July")
    add("August")
    add("September")
    add("October")
    add("November")
    add("December")
}

fun nowDate() : Triple<Int,Int,Int>{
    val ca = Calendar.getInstance()
    val year = ca[Calendar.YEAR]
    val month = ca[Calendar.MONTH]
    val day = ca[Calendar.DAY_OF_MONTH]
    return Triple(year,month,day)
}

fun netToast() = R.string.please_check_the_network.toastShort