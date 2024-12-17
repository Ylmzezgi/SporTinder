package com.ezgiyilmaz.sporfinder.pages

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ezgiyilmaz.sporfinder.Adapters.MessagesAdapter
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
    private lateinit var messageAdapter: MessagesAdapter
    private val messages=ArrayList<Messages>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessagesAdapter(messages)
        binding.messagesRecyclerView.adapter=messageAdapter



        val matchCreatorId = intent.getStringExtra("matchCreatorId") ?: ""
        val matchid = intent.getStringExtra("rivalId") ?: ""

        Log.d("TAG", "matchİdMessgePage: "+matchid)
        binding.sendButton.setOnClickListener {
            sendMessage(matchCreatorId,matchid)
        }
    }

    private fun sendMessage(matchCreatorId: String, matchId: String) {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val messageEditText = binding.messageEditText.text.toString().trim()

        if (messageEditText.isEmpty()) {
            Toast.makeText(this, "Lütfen bir mesaj girin.", Toast.LENGTH_LONG).show()
            return
        }



       val message = Messages(
            userId = currentUser!!.uid,
            sendId = currentUser!!.uid,
            matchId = matchId,
            messages = messageEditText
        )
        db.collection("messages").add(message).addOnSuccessListener {
            Toast.makeText(this, "Mesaj gönderildi!", Toast.LENGTH_LONG).show()
            binding.messageEditText.text.clear()
            messages.add(message)
            messageAdapter.notifyDataSetChanged()
        }.addOnFailureListener {
            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }
}
