package com.ezgiyilmaz.sporfinder.Adapters

import com.google.firebase.Timestamp

data class chatsUser(
    val name: String?="",
    val lastMessage:String?="",
    val timestamp: Timestamp?=null
    )
