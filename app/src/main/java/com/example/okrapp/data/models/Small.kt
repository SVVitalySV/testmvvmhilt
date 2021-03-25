package com.example.okrapp.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Small(
    @Json(name = "https")
    val https: String
)