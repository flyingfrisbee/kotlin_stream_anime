package com.example.streamanime.domain.model

import com.example.streamanime.core.utils.toTimestamp
import com.google.gson.annotations.SerializedName

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
    this.updatedAtTimestamp = this.updatedAt.toTimestamp()
}
