package com.example.okrapp.data.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Filters(
    @Json(name = "category")
    val category: Boolean?,
    @Json(name = "exclude")
    val exclude: Boolean?
)