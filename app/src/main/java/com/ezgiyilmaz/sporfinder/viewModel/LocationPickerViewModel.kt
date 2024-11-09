package com.ezgiyilmaz.sporfinder.viewModel

import androidx.lifecycle.ViewModel
import com.ezgiyilmaz.sporfinder.models.city
import com.ezgiyilmaz.sporfinder.serviceHelper.getService
import com.ezgiyilmaz.sporfinder.services.retrofitService
import retrofit2.await

class LocationPickerViewModel:ViewModel(){
    private lateinit var apiInterface: getService
    lateinit var cities: List<String>
    lateinit var townShips: List<String>
    lateinit var cityId: List<city>
    fun getApiInterface() {

        apiInterface = retrofitService.getInstance().create(getService::class.java)
        //API bağlantısını kurar ve retrofitService ile getService arayüzünü alır.
    }


    suspend fun getCity() {
        val response = apiInterface.getCity().await() //şehir verisini alır.
        cities = response.orEmpty()
            .firstOrNull { it.name == "il" }?.data  // Access the "il" data
            ?.mapNotNull { it.name.trim() } ?: emptyList()

        cityId = response.orEmpty()
            .firstOrNull { it.name == "il" }?.data  // Access the "il" data
            ?.mapNotNull { city(it.id, it.name) } ?: emptyList()

       cities=cities.sorted()

    }


    suspend fun getTownShip(ilId: String?) {
        val response = apiInterface.getTownShip().await() // Execute the call once
        townShips = response.orEmpty()
            .firstOrNull { it.name == "ilce" }?.data  // Access the "il" data
            ?.filter { it.il_id == ilId }?.map { it.name.trim() } ?: emptyList()


         townShips= townShips.sorted()

    }
}