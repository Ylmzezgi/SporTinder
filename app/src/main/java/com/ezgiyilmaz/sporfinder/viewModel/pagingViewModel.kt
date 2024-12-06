import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ezgiyilmaz.sporfinder.Adapters.PagingSource
import com.ezgiyilmaz.sporfinder.models.FilterCriteria
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow

class PagingViewModel : ViewModel() {
//verileri sayfa sayfa çekmek için Paging kütüphanesini kullanan bir ViewModel içeriğidir.
    private val firestore = FirebaseFirestore.getInstance()
//oyuncuBul koleksiyonundaki verileri sayfa sayfa çekmek için kullanılan bir Flow oluşturur.Yani veri değiştikçe UI güncellenir.


    fun pagingData(criteria:FilterCriteria,whicOne:String): Flow<PagingData<Any>> {
        if (whicOne=="oyuncuBul"){
             val playerPagingData: Flow<PagingData<Any>> = Pager(

                config = PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { PagingSource(firestore ,"oyuncuBul",criteria) }
            ).flow.cachedIn(viewModelScope)
            return playerPagingData
        } else{
             val rivalPagingData: Flow<PagingData<Any>> = Pager(
                config = PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { PagingSource(firestore,"rakipBul",criteria) }
            ).flow.cachedIn(viewModelScope)

            return rivalPagingData
        }

    }
}