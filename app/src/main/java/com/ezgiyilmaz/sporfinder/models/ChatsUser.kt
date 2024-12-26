package com.ezgiyilmaz.sporfinder.models

import com.google.firebase.Timestamp

data class chatsUser(
    val name: String?="",
    val lastMessage:String?="",
    val timestamp: Timestamp?=null
    )
