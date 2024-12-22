package com.ezgiyilmaz.sporfinder.models

import android.media.midi.MidiReceiver
import com.google.firebase.Timestamp

data class Messages(
    val receiver: String,
    val sendId:String,
    val messages:String,
    val timestamp: Timestamp?=null
)
