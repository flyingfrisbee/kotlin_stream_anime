package com.example.streamanime.domain.model.enumerate

sealed class LoadingType(val isLoading: Boolean) {
    class LoadAnimeDetail(isLoading: Boolean) : LoadingType(isLoading)
    class BookmarkAnime(isLoading: Boolean) : LoadingType(isLoading)
}