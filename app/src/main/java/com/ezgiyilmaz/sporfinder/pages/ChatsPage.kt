package com.ezgiyilmaz.sporfinder.pages

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezgiyilmaz.sporfinder.Adapters.ChatsAdapter
import com.ezgiyilmaz.sporfinder.Adapters.chatsUser
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ActivityChatsPageBinding
import com.ezgiyilmaz.sporfinder.models.Messages
import com.ezgiyilmaz.sporfinder.models.Register
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatsPage : AppCompatActivity() {
    private lateinit var binding: ActivityChatsPageBinding
    private lateinit var db: FirebaseFirestore
    private var auth = FirebaseAuth.getInstance()
    private lateinit var chatsAdapter: ChatsAdapter
    private val chatUser = ArrayList<chatsUser>()
    private var namee: String? = null
    private var message: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val persons = intent.getStringExtra("persons") ?: ""
        Log.d("persons", "onCreate: persons"+persons)

        binding.recyclerViewId.layoutManager = LinearLayoutManager(this)
        chatsAdapter = ChatsAdapter(chatUser)
        binding.recyclerViewId.adapter = chatsAdapter

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
                                    namee = it.getString("name")
                                }

                            }.addOnFailureListener {
                                it.localizedMessage
                            }

                    }


                }
            }
        }
   
    }

}