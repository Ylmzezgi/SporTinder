package com.ezgiyilmaz.sporfinder.models

data class TownShips (
    val type: String,
    val version: String? = null,
    val comment: String? = null,
    val name: String? = null,
    val database: String? = null,
    val data: List<TownShip>? = null
)

data class TownShip (
    val id: String,
    val il_id: String,
    val name:String
)