package com.ezgiyilmaz.sporfinder.pages

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezgiyilmaz.sporfinder.Adapters.ChatsAdapter
import com.ezgiyilmaz.sporfinder.models.chatsUser
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ActivityChatsPageBinding
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
    private var messagesPerson :String?=null
    private var selectedImage:String?=null
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
                                    selectedImage = it.getString("selectedImage")
                                    println(namee)
                                    println(selectedImage)


                                }

                                var mesajlasankisiler=getConversationId(user, conversationUser)
                                Log.d("TAG", "onCreate:mesajlasankisiler   "+mesajlasankisiler)
                                db.collection("chats").document(mesajlasankisiler!!).collection("chat").orderBy("timestamp",Query.Direction.DESCENDING).limit(1).get().addOnSuccessListener { doc->
                                    for(documents in doc){
                                        messagesPerson=documents.getString("messages")
                                        Log.d("doc", "onCreate: doc"+messagesPerson)
                                        println(doc)
                                    }
                                }
                                val chatsmessage=chatsUser(
                                    name=namee,
                                    lastMessage=messagesPerson,
                                    selectedImage =selectedImage
                                )
                                Log.d("TAG", "onCreate: selected"+messagesPerson)
                                chatUser.add(chatsmessage)
                                chatsAdapter.notifyDataSetChanged()
                            }.addOnFailureListener {
                                it.localizedMessage
                            }
                    }
                }
            }
        }

        }
    }


    fun getConversationId(senderId: String, matchCreatorId: String): String {
        return if (senderId < matchCreatorId) {
            "$senderId$matchCreatorId"
        } else {
            "$matchCreatorId$senderId"
        }
    }