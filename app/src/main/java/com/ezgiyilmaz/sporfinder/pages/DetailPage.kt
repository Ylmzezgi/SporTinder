package com.ezgiyilmaz.sporfinder.pages


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ActivityDetailPageBinding
import com.ezgiyilmaz.sporfinder.models.GetPlayerModel
import com.ezgiyilmaz.sporfinder.models.GetRivalModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class DetailPage : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPageBinding
    private val db = FirebaseFirestore.getInstance()
    var category = ""
    private var auth=FirebaseAuth.getInstance()

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
                    val creatorUserId = player!!.userid
                    println("Maçı oluşturan kişinin ID'si: $creatorUserId")


                    intent.putExtra("creatorUserId", creatorUserId)
                    Log.d("creatorUserId", "onCreate:Maçı oluşturan kişinin ID'si " + creatorUserId)
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
                    val matchIdRival = document.id
                    Log.d("documentid", "onCreate: documnetid" + matchIdRival)
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



        binding.messageButton.setOnClickListener {
            db.collection("oyuncuBul").document(matchid!!).get().addOnSuccessListener { document ->
                println("oyuncu Bul" + document)
                if (document != null) {
                    val player = document.toObject(GetPlayerModel::class.java)
                    val creatorUserId = player!!.userid
                    println("Maçı oluşturan kişinin ID'si: $creatorUserId")
                    val user = auth.currentUser!!.uid
                    if (user != null) {
                        val intent = Intent(this, MessagesPage::class.java).apply {
                            putExtra("rivalId", rivalId)
                            putExtra("creatorUserId", creatorUserId)
                            Log.d("creatorUserId", "onCreate:creatorUserId " + creatorUserId)

                        }
                        startActivity(intent)
                    } else {
                      Toast.makeText(this,"Lütfen Giriş Yapınız",Toast.LENGTH_LONG).show()
                    }
                }
            }
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
}