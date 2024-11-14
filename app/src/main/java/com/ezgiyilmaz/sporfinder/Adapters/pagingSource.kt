    package com.ezgiyilmaz.sporfinder.Adapters

    import android.content.Intent
    import androidx.paging.PagingSource
    import androidx.paging.PagingState
    import com.ezgiyilmaz.sporfinder.models.GetPlayerModel
    import com.ezgiyilmaz.sporfinder.models.GetRivalModel
    import com.ezgiyilmaz.sporfinder.pages.DetailPage
    import com.google.firebase.firestore.FirebaseFirestore
    import com.google.firebase.firestore.Query
    import kotlinx.coroutines.tasks.await

    class PagingSource(
        private val firestore: FirebaseFirestore,
        private val whichOne: String,
        private val pageSize: Int = 1

    ) : PagingSource<Query, Any>() {

        override suspend fun load(params: LoadParams<Query>): LoadResult<Query, Any> {
            return try {
                // İlk sayfa için sorguyu oluştur
                val currentQuery = params.key ?: firestore.collection(whichOne)
                    .orderBy("dateTime", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                println("currentQuery oluşturuldu: $currentQuery")

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

                    }else {
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
                val nextQuery = if (snapshot.size() < pageSize) {
                    null
                } else {
                    firestore.collection(whichOne)
                        .orderBy("dateTime", Query.Direction.DESCENDING)
                        .startAfter(snapshot.documents.last())
                        .limit(pageSize.toLong())
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
