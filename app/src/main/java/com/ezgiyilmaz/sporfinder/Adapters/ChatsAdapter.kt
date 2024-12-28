package com.ezgiyilmaz.sporfinder.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ChatsItemBinding
import com.ezgiyilmaz.sporfinder.models.chatsUser
import com.squareup.picasso.Picasso

class ChatsAdapter(var chatsList: List<chatsUser>):RecyclerView.Adapter<ChatsAdapter.ChatsHolder>() {

    class ChatsHolder(val chatsItemBinding: ChatsItemBinding):RecyclerView.ViewHolder(chatsItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsHolder {
        val binding=ChatsItemBinding.inflate(LayoutInflater.from(parent.context))
        return ChatsHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatsHolder, position: Int) {
        val chat=chatsList[position]
        holder.chatsItemBinding.nameTextView.text=chat.name
        holder.chatsItemBinding.lastmessageTextview.text=chat.lastMessage
        val posterUrl = "${chat.selectedImage}"
        if (holder.chatsItemBinding.circleImage != null) {
            Picasso.get()
                .load(posterUrl)
                .placeholder(R.drawable.ic_launcher_background) // Yüklenirken gösterilecek resim
                .error(R.drawable.user) // Yüklenemezse gösterilecek resim
                .into(holder.chatsItemBinding.circleImage)
        }
    }

    override fun getItemCount(): Int {
        return chatsList.size
    }
}