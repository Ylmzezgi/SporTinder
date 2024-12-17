import android.content.Intent
import android.util.Log

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ListitemBinding
import com.ezgiyilmaz.sporfinder.models.FilterCriteria
import com.ezgiyilmaz.sporfinder.models.GetPlayerModel
import com.ezgiyilmaz.sporfinder.models.GetRivalModel
import com.ezgiyilmaz.sporfinder.pages.DetailPage
import java.text.SimpleDateFormat
import java.util.Locale

class RivalAdapter : PagingDataAdapter<Any, RivalAdapter.MovieViewHolder>(MOVIE_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ListitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val match = getItem(position)
        if (match != null) {
            holder.bind(match)

            holder.itemView.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, DetailPage::class.java).apply {
                    if (match is GetPlayerModel) {
                        putExtra("playerId", match.id)
                        putExtra("matchCreatorId",match.userid)
                    } else {
                        match as GetRivalModel
                        putExtra("rivalId", match.id)
                        putExtra("matchCreatorId",match.userid)
                    }
                }
                context.startActivity(intent)
            }
        }
    }

    class MovieViewHolder(private val binding: ListitemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(match: Any) {
            var category = ""

            if (match is GetRivalModel) {
                println("GetRivalModel bulundu: ${match.category}")
                category = match.category
                binding.playItemTextview.text = match.category
                binding.locationItemTextView.text = match.city
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val formattedDate = dateFormat.format(match.dateTime!!.toDate())
                binding.dateItemTextView.text = formattedDate
            } else {
                match as GetPlayerModel
                println("GetPlayerModel bulundu: ${match.category}")
                Log.d("adapterplayer61", "bind: getplayerbulundu "+match.category)
                category = match.category
                binding.playItemTextview.text = match.category
                binding.locationItemTextView.text = match.city
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val formattedDate = dateFormat.format(match.dateTime!!.toDate())
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
                return result
            }
        }
    }
}