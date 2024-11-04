package com.ezgiyilmaz.sporfinder.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object retrofitService {

    private const val BASE_URL =
        "https://raw.githubusercontent.com/volkansenturk/turkiye-iller-ilceler/refs/heads/master/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

}