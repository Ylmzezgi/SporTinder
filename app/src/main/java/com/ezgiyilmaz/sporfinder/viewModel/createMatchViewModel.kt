package com.ezgiyilmaz.sporfinder.viewModel

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.ezgiyilmaz.sporfinder.models.playerModel
import com.ezgiyilmaz.sporfinder.models.rivalModel
import com.ezgiyilmaz.sporfinder.pages.MessagesPage
import com.ezgiyilmaz.sporfinder.serviceHelper.firebaseMatchHelper
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

class CreateMatchViewModel : ViewModel() {
    val firebaseMatchHelper = firebaseMatchHelper()
    val auth = FirebaseAuth.getInstance()

    suspend fun saveViewModel(
        selected: String,
        Category: String,
        lookingFor: String,
        date: String,
        time: String,
        cal: Timestamp,
        city: String,
        townShips: String,
        note: String

    ): String {
        if (auth.currentUser == null)
            return "Giriş yapınız"

        if (Category.isNullOrEmpty())
            return "Kategori Seçmediniz"

        if (lookingFor.isNullOrEmpty() && selected == "player")
            return "Mevki Seçmediniz"

        if (date.isNullOrEmpty())
            return "Tarih bilgisi girmediniz"

        if (time.isNullOrEmpty())
            return "Saat alanı boş olamaz"

        if (city.isNullOrEmpty())
            return "Şehir alanı boş olamaz"

        if (townShips.isNullOrEmpty())
            return "ilçe alanı boş olamaz"

        if (selected.isNullOrEmpty())
            return "Seçim Yapınız"

        if (selected == "rival") {
            val rivalModel =
                rivalModel(auth.currentUser!!.uid, Category, cal, city, townShips, note)
            val message = firebaseMatchHelper.rivalAddFirebase(rivalModel)
            return message
        } else {
            val playerModel = playerModel(
                auth.currentUser!!.uid,
                Category,
                lookingFor,
                cal,
                city,
                townShips,
                note
            )
            val message = firebaseMatchHelper.playerAddFirebase(playerModel)
            return message
        }
    }
}