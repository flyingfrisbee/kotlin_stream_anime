package com.example.streamanime.domain.model

import android.text.format.DateUtils
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat

data class SyncBookmarkData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("latest_episode")
    val latestEpisode: String,
    @SerializedName("updated_at")
    val updatedAt: String,

    var updatedAtTimestamp: Long,
)

fun SyncBookmarkData.getTimestamp() {
    val timeInMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ").parse(this.updatedAt).time
    this.updatedAtTimestamp = timeInMillis
}
