package com.example.streamanime.domain.model

import com.google.gson.annotations.SerializedName

data class AnimeDetailData(
    val airingStatus: String,
    val animeId: String? = null,
    val episodeList: List<EpisodeData>,
    val genre: String,
    val imageUrl: String,
    val internalId: String,
    val releasedDate: String,
    val summary: String,
    val title: String,
    val type: String,
)

data class EpisodeData(
    val episodeForEndpoint: String,
    val episodeForUi: String,
    var clicked: Boolean = false,
)
