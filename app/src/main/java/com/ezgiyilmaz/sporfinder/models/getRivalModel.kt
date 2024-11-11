package com.ezgiyilmaz.sporfinder.models

import com.google.firebase.Timestamp

data class GetRivalModel(
    val userid:String="",
    val category:String="",
    val city:String="",
    val dateTime:Timestamp,
    val note:String="",
    val townShip:String,
    val documentId:String
)
