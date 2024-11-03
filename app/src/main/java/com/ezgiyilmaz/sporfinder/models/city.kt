package com.ezgiyilmaz.sporfinder.models

import kotlinx.serialization.Serializable

@Serializable
data class cities(
    val type: String,
    val version: String?,
    val comment: String?,
    val name: String?,
    val database: String?,
    val data: List<city>?,
)
@Serializable
data class city(
    val id: String,
    val name: String,
)
