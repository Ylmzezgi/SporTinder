import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ListitemBinding
import com.ezgiyilmaz.sporfinder.models.GetPlayerModel
import com.ezgiyilmaz.sporfinder.models.GetRivalModel
import java.text.SimpleDateFormat
import java.util.Locale

class RivalAdapter (var onItemClick:Any): PagingDataAdapter<Any, RivalAdapter.MovieViewHolder>(MOVIE_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        println("ViewHolder oluşturuluyor")
        val binding = ListitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val match = getItem(position)
        if (match != null) {
            println("ViewHolder ${position}. pozisyona bağlanıyor")
            holder.bind(match)
        } else {
            println("${position}. pozisyonda öğe null geldi")
        }
    }

    class MovieViewHolder(private val binding: ListitemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(rival: Any) {
            println("bind fonksiyonu çağrıldı")
            var category = ""

            if (rival is GetRivalModel) {
                println("GetRivalModel bulundu: ${rival.category}")
                category = rival.category
                binding.playItemTextview.text = rival.category
                binding.locationItemTextView.text = rival.city
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val formattedDate = dateFormat.format(rival.dateTime!!.toDate())
                binding.dateItemTextView.text = formattedDate
            }
            else {
                rival as GetPlayerModel
                println("GetPlayerModel bulundu: ${rival.category}")
                category = rival.category
                binding.playItemTextview.text = rival.category
                binding.locationItemTextView.text = rival.city
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val formattedDate = dateFormat.format(rival.dateTime!!.toDate())
                binding.dateItemTextView.text = formattedDate

            }


            // Kategoriye göre görsel ayarlama
            when (category) {
                "Futbol" -> {
                    println("Kategori Futbol, görsel ayarlandı")
                    binding.imageView2.setImageResource(R.drawable.football)
                    binding.playItemTextview.setOnClickListener {
                        println("Futbol öğesine tıklandı")
                    }
                }
                "Basketbol" -> {
                    println("Kategori Basketbol, görsel ayarlandı")
                    binding.imageView2.setImageResource(R.drawable.basketball)
                }
                "Tenis" -> {
                    println("Kategori Tenis, görsel ayarlandı")
                    binding.imageView2.setImageResource(R.drawable.tennis)
                }
                "Voleybol" -> {
                    println("Kategori Voleybol, görsel ayarlandı")
                    binding.imageView2.setImageResource(R.drawable.volleyball)
                }
                else -> {
                    println("Kategori tanınmadı")
                }

            }

        }

    }

    companion object {
        private val MOVIE_COMPARATOR = object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                val result = if (oldItem is GetRivalModel) {
                    newItem as GetRivalModel
                    oldItem.id == newItem.id
                } else {
                    oldItem as GetPlayerModel
                    newItem as GetPlayerModel
                    oldItem.id == newItem.id
                }
                println("areItemsTheSame sonucu: $result")
                return result
            }

            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                val result = if (oldItem is GetRivalModel) {
                    newItem as GetRivalModel
                    oldItem == newItem
                } else {
                    oldItem as GetPlayerModel
                    newItem as GetPlayerModel
                    oldItem == newItem
                }
                println("areContentsTheSame sonucu: $result")
                return result
            }
        }
    }
}
