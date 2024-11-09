package com.ezgiyilmaz.sporfinder.models

import com.google.firebase.Timestamp

data class playerModel(
    val Category: String,
    val lookingFor: String,
    val dateTime: Timestamp,
    val city: String,
    val townShip: String,
    val note: String
)
