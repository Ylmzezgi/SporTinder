package com.ezgiyilmaz.sporfinder.models

import com.google.firebase.Timestamp

data class rivalModel(
    val userid:String,
    val Category: String,
    val dateTime: Timestamp,
    val city: String,
    val townShip: String,
    val note: String
)
