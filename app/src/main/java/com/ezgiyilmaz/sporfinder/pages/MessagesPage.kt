package com.ezgiyilmaz.sporfinder.pages

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezgiyilmaz.sporfinder.Adapters.MessagesAdapter
import com.ezgiyilmaz.sporfinder.databinding.ActivityMessagesPageBinding
import com.ezgiyilmaz.sporfinder.models.Messages
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MessagesPage : AppCompatActivity() {
    private lateinit var binding: ActivityMessagesPageBinding
    private var db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var messageAdapter: MessagesAdapter
    private val messages = ArrayList<Messages>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()
        val matchid = intent.getStringExtra("rivalId") ?: ""
        val matchCreatorId = intent.getStringExtra("creatorUserId") ?: ""

        Log.d("matchCreatorId", "onCreate: matchcreatorıd" + matchCreatorId)

        val senderId = auth.currentUser!!.uid
        val conversationId = getConversationId(senderId, matchCreatorId)
        intent.putExtra("persons",conversationId)

        Log.d("TAG", "matchİdMessgePage: " + matchid)

        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessagesAdapter(messages, senderId)
        binding.messagesRecyclerView.adapter = messageAdapter
        auth = FirebaseAuth.getInstance()


        binding.sendButton.setOnClickListener {
            sendMessage(matchCreatorId, senderId, conversationId)
        }
        if(senderId!=null){
        db.collection("user").document(senderId).get().addOnSuccessListener { document->
            if(document!=null) {
                val name = document.getString("name")
                binding.nameTextView.text = name
            }
            }
        }

        val user=Firebase.auth.currentUser
        user?.let {
            val name=it.displayName
            binding.nameTextView.text=name
            println(name)

        }
    }

    fun getConversationId(senderId: String, matchCreatorId: String): String {
        return if (senderId < matchCreatorId) {
            "$senderId$matchCreatorId"
        } else {
            "$matchCreatorId$senderId"
        }
    }


    private fun sendMessage(matchCreatorId: String, senderId: String, conversationId: String) {

        val messageEditText = binding.messageEditText.text.toString().trim()

        if (messageEditText.isEmpty()) {
            Toast.makeText(this, "Lütfen bir mesaj girin.", Toast.LENGTH_LONG).show()
            return
        } else {
            val document = db.collection("user").document(senderId)
            if (senderId!=null) {
                document.update(
                    "conversationUsers",
                    FieldValue.arrayUnion(matchCreatorId)
                ) // fieldvalue varsa bu id tekrar kaydetmeyi engelliyor
                    .addOnSuccessListener {
                        Toast.makeText(this, "conversationUsers kaydedildi", Toast.LENGTH_LONG)
                            .show()

                    }.addOnFailureListener {
                        it.localizedMessage
                    }
            }
            val documentReceiver=db.collection("user").document(matchCreatorId)
            documentReceiver.update("conversationUsers", FieldValue.arrayUnion(senderId)).addOnSuccessListener {
                Toast.makeText(this, "conversationUsers kaydedildi", Toast.LENGTH_LONG).show()

            }.addOnFailureListener{
                it.localizedMessage
            }
        }

        val message = Messages(
            receiver = matchCreatorId,
            sendId = senderId,
            messages = messageEditText,
            timestamp = Timestamp.now()
        )

        db.collection("chats").document(conversationId)
            .collection("chat")
            .add(message).addOnSuccessListener {
                Toast.makeText(this, "Mesaj gönderildi!", Toast.LENGTH_LONG).show()

                binding.messageEditText.text.clear()
                messages.add(message)
                messageAdapter.notifyDataSetChanged()
            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }
}
