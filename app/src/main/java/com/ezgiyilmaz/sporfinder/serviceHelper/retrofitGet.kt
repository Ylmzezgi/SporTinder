package com.ezgiyilmaz.sporfinder.serviceHelper
import com.ezgiyilmaz.sporfinder.models.TownShips
import com.ezgiyilmaz.sporfinder.models.cities
import retrofit2.Call
import retrofit2.http.GET

interface getService {

    @GET("il.json")
    fun getCity(): Call<List<cities>>

    @GET("ilce.json")
    fun getTownShip():Call<List<TownShips>>

}