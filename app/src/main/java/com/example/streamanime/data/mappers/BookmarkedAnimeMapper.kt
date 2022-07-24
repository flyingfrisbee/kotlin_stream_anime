package com.example.streamanime.data.mappers

import com.example.streamanime.data.remote.dto.response.BookmarkedAnimeResponse
import com.example.streamanime.domain.model.BookmarkedAnimeData

fun BookmarkedAnimeResponse.toData(): BookmarkedAnimeData {
    return BookmarkedAnimeData(
        internalId = internalId,
        title = title,
        latestEpisode = latestEpisode,
        timestamp = System.currentTimeMillis(),
        haveNewUpdate = true,
        imageUrl = imageUrl
    )
}