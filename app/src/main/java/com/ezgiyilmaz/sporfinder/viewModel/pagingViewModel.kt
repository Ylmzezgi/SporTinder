import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ezgiyilmaz.sporfinder.Adapters.PagingSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class PagingViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    val playerPagingData: Flow<PagingData<Any>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { PagingSource(firestore,"oyuncuBul") }
    ).flow.cachedIn(viewModelScope)


    val rivalPagingData: Flow<PagingData<Any>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { PagingSource(firestore,"rakipBul") }
    ).flow.cachedIn(viewModelScope)

}
