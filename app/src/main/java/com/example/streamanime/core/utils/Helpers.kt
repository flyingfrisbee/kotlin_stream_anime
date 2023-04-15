package com.example.streamanime.core.utils

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat

fun View.setToVisible() {
    visibility = View.VISIBLE
}

fun View.setToGone() {
    visibility = View.GONE
}

fun String.toTimestamp(): Long {
    return try {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'").parse(this).time
    } catch (e: Exception) {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ").parse(this).time
    }
}