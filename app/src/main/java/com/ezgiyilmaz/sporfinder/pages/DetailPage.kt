package com.ezgiyilmaz.sporfinder.pages

import PagingViewModel
import RivalAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ezgiyilmaz.sporfinder.Adapters.PagingSource
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ActivityDetailPageBinding
import com.ezgiyilmaz.sporfinder.models.GetPlayerModel
import com.ezgiyilmaz.sporfinder.models.GetRivalModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.crypto.EncryptedPrivateKeyInfo

class DetailPage : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPageBinding
    private val db = FirebaseFirestore.getInstance()
    var category = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val matchid = intent.getStringExtra("playerId")
        val rivalId = intent.getStringExtra("rivalId")

        try {
            db.collection("oyuncuBul").document(matchid!!).get().addOnSuccessListener { document ->
                println("oyuncu Bul" + document)
                if (document != null) {
                    val player = document.toObject(GetPlayerModel::class.java)
                    val matchCreatorId = document.getString("creatorId") ?: ""
                    intent.putExtra("matchCreatorId",matchCreatorId)
                    Log.d("document", "onCreate: " +matchCreatorId)
                    category = player!!.category
                    binding.categoryDetailTextView.text = player?.category
                    binding.dateTimeDetailTextView.text =
                        "Tarih ve Saat : " + player?.dateTime!!.toDate().toString()
                    binding.cityDetailTextView.text = "İl : " + player?.city
                    binding.townShipDetailTextView.text = "İlçe : " + player?.townShip
                    binding.lookingForDetailTextView.text = "Mevki " + player?.lookingFor
                    binding.noteDetailTextView.text = "Not " + player?.note

                    getImage()

                }
            }
        } catch (e: Exception) {
            e
        }

        println("rivalid" + rivalId)
        try {
            db.collection("rakipBul").document(rivalId!!).get().addOnSuccessListener { document ->
                println("rakip bul documneti = " + document)

                if (document != null) {
                    println("rakip bul documneti 2 = " + document)

                    val rival = document.toObject(GetRivalModel::class.java)
                    println("rakip bul rival = " + rival)

                    category = rival!!.category
                    binding.categoryDetailTextView.text = rival?.category
                    binding.dateTimeDetailTextView.text =
                        "Tarih ve Saat : " + rival?.dateTime!!.toDate().toString()
                    binding.cityDetailTextView.text = "İl : " + rival?.city
                    binding.townShipDetailTextView.text = "İlçe : " + rival?.townShip
                    binding.noteDetailTextView.text = "Not " + rival?.note

                    getImage()

                }
            }
        } catch (e: Exception) {
            e
        }


        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun getImage() {
        when (category) {
            "Futbol" -> {
                println("Kategori Futbol, görsel ayarlandı")
                binding.detailImage.setImageResource(R.drawable.football)

            }

            "Basketbol" -> {
                println("Kategori Basketbol, görsel ayarlandı")
                binding.detailImage.setImageResource(R.drawable.basketball)
            }

            "Tenis" -> {
                println("Kategori Tenis, görsel ayarlandı")
                binding.detailImage.setImageResource(R.drawable.tennis)
            }

            "Voleybol" -> {
                println("Kategori Voleybol, görsel ayarlandı")
                binding.detailImage.setImageResource(R.drawable.volleyball)
            }

            else -> {
                println("Kategori tanınmadı")
            }
        }
    }

    fun messageClick(view:View){
        Intent(this,MessagesPage::class.java).also {

            startActivity(intent)
        }
    }
}