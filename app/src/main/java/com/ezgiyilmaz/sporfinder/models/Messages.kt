package com.ezgiyilmaz.sporfinder.models

import com.google.firebase.Timestamp

data class Messages(
    val userId:String,
    val sendId:String,
    val matchId:String,
    val messages:String,

)
