package com.ezgiyilmaz.sporfinder.pages

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ActivityHomePageBinding
import com.ezgiyilmaz.sporfinder.databinding.ActivityMessagesPageBinding
import com.ezgiyilmaz.sporfinder.models.Messages
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MessagesPage : AppCompatActivity() {
    private lateinit var binding: ActivityMessagesPageBinding
    private var db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        setContentView(R.layout.activity_messages_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser


        val matchid = intent.getStringExtra("playerId")?:""
        val matchCreatorId = intent.getStringExtra("matchCreatorId")?:""



    val message=Messages(
        userId = currentUser!!.uid,
        sendId = matchCreatorId,
        matchId =matchid,
        messages ="sfeew",


    )
}
}