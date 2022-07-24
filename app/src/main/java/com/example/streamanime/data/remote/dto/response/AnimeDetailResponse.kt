package com.example.streamanime.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class AnimeDetailResponse(
    @SerializedName("airing_status")
    val airingStatus: String,
    @SerializedName("anime_id")
    val animeId: String? = null,
    @SerializedName("episode_list")
    val episodeList: List<Episode>,
    val genre: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("internal_id")
    val internalId: String,
    @SerializedName("released_date")
    val releasedDate: String,
    val summary: String,
    val title: String,
    val type: String,
    @SerializedName("updated_timestamp")
    val updatedTimestamp: Long
)

data class Episode(
    @SerializedName("episode_for_endpoint")
    val episodeForEndpoint: String,
    @SerializedName("episode_for_ui")
    val episodeForUi: String
)