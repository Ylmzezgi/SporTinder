package com.ezgiyilmaz.sporfinder.pages

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezgiyilmaz.sporfinder.Adapters.ChatsAdapter
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ActivityProfilePageBinding
import com.ezgiyilmaz.sporfinder.models.chatsUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfilePage : AppCompatActivity() {
    private lateinit var binding: ActivityProfilePageBinding
    private lateinit var db: FirebaseFirestore
    private var auth = FirebaseAuth.getInstance()
    private lateinit var chatsAdapter: ChatsAdapter
    private val chatUser = ArrayList<chatsUser>()
    private var namee: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        db = FirebaseFirestore.getInstance()


        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        chatsAdapter = ChatsAdapter(chatUser)
        binding.recyclerView.adapter = chatsAdapter

        val user = auth.currentUser!!.uid



        db.collection("user").document(user).get().addOnSuccessListener { document ->
            if (document != null) {
                val conversationUsers = document.get("conversationUsers") as? List<String>
                if (conversationUsers != null) {
                    for (conversationUser in conversationUsers) {

                        Log.d(
                            "conversationUsers",
                            "chatsShow: conversationUsers" + conversationUser
                        )
                        db.collection("user").document(conversationUser).get()
                            .addOnSuccessListener {
                                if (it != null) {
                                    binding.nameTextView.text = it.getString("name")
                                    val image = it.getString("selectedImage")
                                    println(namee)
                                    println(image)


                                }

                            }
                    }
                }
            }
        }
    }
}