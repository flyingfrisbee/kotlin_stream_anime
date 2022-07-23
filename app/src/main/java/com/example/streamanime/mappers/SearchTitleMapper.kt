package com.example.streamanime.mappers

import com.example.streamanime.data.remote.dto.response.SearchTitleResponse
import com.example.streamanime.domain.model.SearchTitleData

fun SearchTitleResponse.toData(): SearchTitleData {
    return SearchTitleData(
        animeId = animeId,
        title = title
    )
}