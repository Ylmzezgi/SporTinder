package com.ezgiyilmaz.sporfinder.models

import com.google.firebase.Timestamp

data class GetPlayerModel(
    val id:String="",
    val userid:String="",
    val category:String="",
    val city:String="",
    val dateTime:Timestamp?=null,
    val lookingFor:String="",
    val note:String="",
    val townShip:String=""
)
