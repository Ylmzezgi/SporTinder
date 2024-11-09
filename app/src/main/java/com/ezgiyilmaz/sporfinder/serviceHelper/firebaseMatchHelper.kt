package com.ezgiyilmaz.sporfinder.serviceHelper

import com.ezgiyilmaz.sporfinder.models.playerModel
import com.ezgiyilmaz.sporfinder.models.rivalModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class firebaseMatchHelper {
    private val db=FirebaseFirestore.getInstance()

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
        }catch (e:Exception){
            return e.localizedMessage
        }
    }
}