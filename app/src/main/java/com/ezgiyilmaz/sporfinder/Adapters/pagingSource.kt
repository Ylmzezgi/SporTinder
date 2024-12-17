package com.ezgiyilmaz.sporfinder.Adapters

import android.content.Intent
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ezgiyilmaz.sporfinder.models.FilterCriteria
import com.ezgiyilmaz.sporfinder.models.GetPlayerModel
import com.ezgiyilmaz.sporfinder.models.GetRivalModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class PagingSource(
    private val firestore: FirebaseFirestore,
    private val whichOne: String,
    private val criteria: FilterCriteria,
    private val pageSize: Int = 1
) : PagingSource<Query, Any>() {
    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, Any> {
        return try {
            // İlk sayfa için sorguyu oluştur
            Log.d(
                "Criteria",
                "Criteria values: category=${criteria.category}, lookingFor=${criteria.lookingFor}, city=${criteria.city}, townShip=${criteria.townShip}"
            )

            var query = firestore.collection(whichOne)

                .orderBy("dateTime", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())


            if (!criteria.category.isNullOrEmpty()) {
                query=query.whereEqualTo("category", criteria.category)
                Log.d("deneme", "category: " + query)

            }

                if (!criteria.lookingFor.isNullOrEmpty()) {
                    query = query.whereEqualTo("lookingFor", criteria.lookingFor)
                    Log.d("deneme", "lookingFor: " + query)

                }



            if (!criteria.city.isNullOrEmpty()) {
                query= query.whereEqualTo("city", criteria.city)
                Log.d("deneme", "city: " + query)

            }
            if (!criteria.townShip.isNullOrEmpty()) {
                query=query.whereEqualTo("townShip", criteria.townShip)
                Log.d("deneme", "township: " + query)

            }


            val currentQuery = params.key ?: query

            // Firestore sorgusunu çalıştır
            val snapshot = currentQuery.get().await()
            println("Snapshot getirildi: ${snapshot.documents.size} belge alındı")

            // Belgeleri veri modellerine dönüştür
            val player = snapshot.documents.map { document ->


                if (whichOne == "oyuncuBul") {
                    GetPlayerModel(
                        id = document.id,
                        category = document.getString("category") ?: "",
                        city = document.getString("city") ?: "",
                        dateTime = document.getTimestamp("dateTime"),
                        townShip = document.getString("townShip") ?: "",
                        note = document.getString("note") ?: "",
                        lookingFor = document.getString("lookingFor") ?: "",
                        userid = document.getString("userid") ?: ""
                    )

                } else {
                    GetRivalModel(
                        id = document.id,
                        category = document.getString("category") ?: "",
                        city = document.getString("city") ?: "",
                        dateTime = document.getTimestamp("dateTime"),
                        townShip = document.getString("townShip") ?: "",
                        note = document.getString("note") ?: "",
                        userid = document.getString("userid") ?: ""
                    )

                }

            }

            // Sonraki sorguyu oluştur
            var nextQuery = if (snapshot.size() < pageSize) {
                null
            } else {
                firestore.collection(whichOne)
                    .orderBy("dateTime", Query.Direction.DESCENDING)
                    .startAfter(snapshot.documents.last())
                    .limit(pageSize.toLong())

            }

            if (!criteria.category.isNullOrEmpty()) {
                nextQuery=nextQuery?.whereEqualTo("category", criteria.category)
                Log.d("deneme", "category: " + query)

            }
            if (!criteria.lookingFor.isNullOrEmpty()) {
                nextQuery=nextQuery?.whereEqualTo("lookingFor", criteria.lookingFor)
                Log.d("deneme", "lookingFor: " + query)

            }

            if (!criteria.city.isNullOrEmpty()) {
                nextQuery= nextQuery?.whereEqualTo("city", criteria.city)
                Log.d("deneme", "city: " + query)
            }
            if (!criteria.townShip.isNullOrEmpty()) {
                nextQuery=nextQuery?.whereEqualTo("townShip", criteria.townShip)
                Log.d("deneme", "township: " + query)

            }
            println("Sonraki sorgu (nextQuery): $nextQuery")

            LoadResult.Page(
                data = player,
                prevKey = null,  // Firestore'da önceki sayfa desteği genellikle kullanılmaz
                nextKey = nextQuery
            ).also {
                println("LoadResult.Page oluşturuldu, veri boyutu: ${player.size}")
            }
        } catch (e: Exception) {
            println("Hata oluştu: ${e.message}")
            LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<Query, Any>): Query? {
        println("getRefreshKey çağrıldı, state: $state")
        return null
    }
}