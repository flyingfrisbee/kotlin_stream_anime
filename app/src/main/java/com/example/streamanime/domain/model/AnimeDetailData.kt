package com.example.streamanime.domain.model

import android.text.format.DateUtils
import com.example.streamanime.core.utils.toTimestamp
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat

data class AnimeDetailData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("airing_year")
    val airingYear: String,
    @SerializedName("episodes")
    val episodes: List<EpisodeData>,
    @SerializedName("genre")
    val genre: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("latest_episode")
    val latestEpisode: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("updated_at")
    val updatedAt: String,

    var updatedAtTimestamp: Long? = null
)

data class EpisodeData(
    @SerializedName("endpoint")
    val endpoint: String,
    @SerializedName("text")
    val text: String,

    var clicked: Boolean = false,
)

fun AnimeDetailData.getTimestamp() {
    this.updatedAtTimestamp = this.updatedAt.toTimestamp()
}
