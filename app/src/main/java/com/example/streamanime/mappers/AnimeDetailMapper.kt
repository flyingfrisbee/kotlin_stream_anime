package com.example.streamanime.mappers

import com.example.streamanime.data.remote.dto.response.AnimeDetailResponse
import com.example.streamanime.data.remote.dto.response.Episode
import com.example.streamanime.domain.model.AnimeDetailData
import com.example.streamanime.domain.model.EpisodeData

fun AnimeDetailResponse.toData(): AnimeDetailData {
    return AnimeDetailData(
        airingStatus = airingStatus,
        animeId = animeId,
        episodeList = episodeList.map { it.toData() },
        genre = genre,
        imageUrl = imageUrl,
        internalId = internalId,
        releasedDate = releasedDate,
        summary = summary,
        title = title,
        type = type
    )
}

fun Episode.toData(): EpisodeData {
    return EpisodeData(
        episodeForUi = episodeForUi,
        episodeForEndpoint = episodeForEndpoint
    )
}