package com.ezgiyilmaz.sporfinder.models

import com.google.firebase.Timestamp

data class FilterCriteria(
    val category: String?="",
    val lookingFor: String?="",
    val dateTime: Timestamp?=null,
    val city: String?="",
    val townShip: String?=""
)
