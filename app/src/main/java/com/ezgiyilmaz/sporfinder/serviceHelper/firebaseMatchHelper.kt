package com.ezgiyilmaz.sporfinder.serviceHelper

import com.ezgiyilmaz.sporfinder.models.GetPlayerModel
import com.ezgiyilmaz.sporfinder.models.GetRivalModel
import com.ezgiyilmaz.sporfinder.models.playerModel
import com.ezgiyilmaz.sporfinder.models.rivalModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class firebaseMatchHelper {
    private val db = FirebaseFirestore.getInstance()
    val getRivalList = mutableListOf<GetRivalModel>()
    val getPlayerList = mutableListOf<GetPlayerModel>()
    private val auth=FirebaseAuth.getInstance()

    suspend fun rivalAddFirebase(rivalModel: rivalModel): String {
        try {
            db.collection("rakipBul").add(rivalModel).await()
            return "eklendi"
        } catch (e: Exception) {
            return e.localizedMessage
        }
    }

    suspend fun playerAddFirebase(playerModel: playerModel):String{
            try {
                db.collection("oyuncuBul").add(playerModel).await()
                return "eklendi"
            } catch (e: Exception) {
                return e.localizedMessage
            }

    }


    suspend fun getRivalFirebase(): List<GetRivalModel> {
        try {
            getRivalList.clear()
            db.collection("rakipBul").get().addOnSuccessListener {
                for (document in it) {
                    val getRivalModel = document.toObject(GetRivalModel::class.java).copy(id = document.id)
                    getRivalList.add(getRivalModel)
                }
            }.await()
            return getRivalList
        } catch (e: Exception) {
            return emptyList()
        }
    }


    suspend fun getPlayerFirebase() :List<GetPlayerModel>{
        try {
            getPlayerList.clear()
            db.collection("oyuncuBul").get().addOnSuccessListener { result ->
                for (document in result) {
                    val getPlayerModel= document.toObject(GetPlayerModel::class.java).copy(id = document.id)

                    getPlayerList.add(getPlayerModel)
                }

            }.await()
            return getPlayerList

        } catch (e: Exception) {
            return emptyList()
        }
    }
}