package com.example.streamanime.core.utils

import android.view.View
import java.text.SimpleDateFormat

fun View.setToVisible() {
    visibility = View.VISIBLE
}

fun View.setToGone() {
    visibility = View.GONE
}

fun String.toTimestamp(): Long {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ").parse(this).time
}