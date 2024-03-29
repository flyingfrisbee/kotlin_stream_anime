package com.example.streamanime.domain.model

import com.google.gson.annotations.SerializedName

data class SearchTitleData(
    @SerializedName("endpoint")
    val endpoint: String,
    @SerializedName("title")
    val title: String
)
