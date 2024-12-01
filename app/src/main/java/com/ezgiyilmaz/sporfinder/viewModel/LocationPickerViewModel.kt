package com.ezgiyilmaz.sporfinder.viewModel

import androidx.lifecycle.ViewModel
import com.ezgiyilmaz.sporfinder.models.city
import com.ezgiyilmaz.sporfinder.serviceHelper.getService
import com.ezgiyilmaz.sporfinder.services.retrofitService
import retrofit2.await

class LocationPickerViewModel : ViewModel() {
    private lateinit var apiInterface: getService
    var cities: List<String> = emptyList()
    var townShips: List<String> = emptyList()
    lateinit var cityId: List<city>
    fun getApiInterface() {

        apiInterface = retrofitService.getInstance().create(getService::class.java)
        //API bağlantısını kurar ve retrofitService ile getService arayüzünü alır.
    }


    suspend fun getCity() {
        //API'den şehir bilgilerini alır ve bunları cities ve cityId listelerine dönüştürür.
        val response = apiInterface.getCity().await() //şehir verisini alır.
        cities = response.orEmpty()
            .firstOrNull { it.name == "il" }?.data  // Verilerde "il" adında bir grup arar.
            ?.mapNotNull { it.name.trim() } ?: emptyList() //Şehir isimlerini işler ve listeye ekler.

        cityId = response.orEmpty()
            .firstOrNull { it.name == "il" }?.data  // Access the "il" data
            ?.mapNotNull { city(it.id, it.name) } ?: emptyList()

        cities = cities.sorted() //alfabetik olarak sıralar
        cityId = cityId.sortedBy { it.name } // şehir isimlerine göre sıralar

    }


    suspend fun getTownShip(ilId: String?) {
        val response = apiInterface.getTownShip().await() // Execute the call once
        townShips = response.orEmpty()
            .firstOrNull { it.name == "ilce" }?.data  // Access the "il" data
            ?.filter { it.il_id == ilId }?.map { it.name.trim() } ?: emptyList() //İlçeleri (ilce) filtreler ve yalnızca il_id'si verilen il ile eşleşenleri seçer.


        townShips = townShips.sorted()

    }
}