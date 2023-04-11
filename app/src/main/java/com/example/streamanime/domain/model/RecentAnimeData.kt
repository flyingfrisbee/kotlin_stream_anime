package com.example.streamanime.domain.model

import android.text.format.DateUtils
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat

data class RecentAnimeData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("latest_episode")
    val latestEpisode: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    var updatedAtTimestamp: String
)

fun RecentAnimeData.getTimestamp() {
    val timeInMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ").parse(this.updatedAt).time
    val relativeTime = DateUtils.getRelativeTimeSpanString(timeInMillis, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
    this.updatedAtTimestamp = relativeTime.toString()
}

