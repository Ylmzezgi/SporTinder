package com.ezgiyilmaz.sporfinder.models

import com.google.firebase.Timestamp

data class GetRivalModel(
    val id:String="",
    val userid:String="",
    val category:String="",
    val city:String="",
    val dateTime:Timestamp?=null,
    val note:String="",
    val townShip:String="",
)
