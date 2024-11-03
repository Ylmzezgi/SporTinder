package com.ezgiyilmaz.sporfinder.services
import android.content.Context
import com.ezgiyilmaz.sporfinder.models.cities
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.IOException
class jsonService {

    fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    fun parseCitiesJson(jsonString: String): cities {
        return Json.decodeFromString(jsonString)
    }

}