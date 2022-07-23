package com.example.streamanime.mappers

import com.example.streamanime.data.remote.dto.response.RecentAnimeResponse
import com.example.streamanime.domain.model.RecentAnimeData

fun RecentAnimeResponse.toData(): RecentAnimeData {
    return RecentAnimeData(
        imageUrl = imageUrl,
        internalId = internalId,
        latestEpisode = latestEpisode,
        title = title,
        updatedTimestamp = updatedTimestamp
    )
}