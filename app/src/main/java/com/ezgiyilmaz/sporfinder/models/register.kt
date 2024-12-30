package com.ezgiyilmaz.sporfinder.models

import android.media.Image
import android.net.Uri

data class Register(
    val name:String,
    val surname:String,
    val userName:String,
    val cityLocation:String,
    val townShip:String,
    val selectedImage:String
    )
