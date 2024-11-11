package com.ezgiyilmaz.sporfinder.viewModel

import androidx.lifecycle.ViewModel
import com.ezgiyilmaz.sporfinder.models.GetPlayerModel
import com.ezgiyilmaz.sporfinder.serviceHelper.firebaseMatchHelper
import com.google.firebase.auth.FirebaseAuth

class HomePageViewModel : ViewModel() {
    val firebaseMatchHelper = firebaseMatchHelper()
    suspend fun ClickPlayerViewModel(
        selected: String
    ):HomePageAdapter{

        if(selected=="rival"){
            val gelenListe=firebaseMatchHelper.getRivalFirebase()
            val adapter=HomePageAdapter(gelenListe,"rival")
            return adapter

        }
        if (selected=="player"){
           val gelenListe= firebaseMatchHelper.getPlayerFirebase()
            val adapter=HomePageAdapter(gelenListe,"player")
            return adapter
        }
        return HomePageAdapter(emptyList(),"sporFinder")
        }
    }
