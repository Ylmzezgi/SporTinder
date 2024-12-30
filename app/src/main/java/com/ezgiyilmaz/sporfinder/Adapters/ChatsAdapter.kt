package com.ezgiyilmaz.sporfinder.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ChatsItemBinding
import com.ezgiyilmaz.sporfinder.models.chatsUser
import com.ezgiyilmaz.sporfinder.models.PlayerMatch

import com.squareup.picasso.Picasso

class ChatsAdapter(var chatsList: List<Any>) : RecyclerView.Adapter<ChatsAdapter.ChatsHolder>() {

    class ChatsHolder(val chatsItemBinding: ChatsItemBinding) :
        RecyclerView.ViewHolder(chatsItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsHolder {
        val binding = ChatsItemBinding.inflate(LayoutInflater.from(parent.context))
        return ChatsHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatsHolder, position: Int) {
        val chat = chatsList[position]
        var category = ""
        if (chat is chatsUser) {
            holder.chatsItemBinding.nameTextView.text = chat.name
            holder.chatsItemBinding.lastmessageTextview.text = chat.lastMessage
            val posterUrl = "${chat.selectedImage}"
            if (holder.chatsItemBinding.circleImage != null) {
                Picasso.get()
                    .load(posterUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.user)
                    .into(holder.chatsItemBinding.circleImage)
            }
        } else {
            chat as PlayerMatch
            category = chat.category.toString()
            holder.chatsItemBinding.nameTextView.text = chat.category
            val lookingFor = chat.lookingFor
            if (!lookingFor.isNullOrEmpty()) {
                holder.chatsItemBinding.lastmessageTextview.text = chat.lookingFor

            } else {
                holder.chatsItemBinding.lastmessageTextview.text = "Rakip Bul,Mevki Yok"

            }
        }
        when (category) {
            "Futbol" -> {
                println("Kategori Futbol, görsel ayarlandı")
                holder.chatsItemBinding.circleImage.setImageResource(R.drawable.football)
            }

            "Basketbol" -> {
                println("Kategori Basketbol, görsel ayarlandı")
                holder.chatsItemBinding.circleImage.setImageResource(R.drawable.basketball)
            }

            "Tenis" -> {
                println("Kategori Tenis, görsel ayarlandı")
                holder.chatsItemBinding.circleImage.setImageResource(R.drawable.tennis)
            }

            "Voleybol" -> {
                println("Kategori Voleybol, görsel ayarlandı")
                holder.chatsItemBinding.circleImage.setImageResource(R.drawable.volleyball)
            }

            else -> {
                println("Kategori tanınmadı")
            }
        }
    }

    override fun getItemCount(): Int {
        return chatsList.size
    }
}