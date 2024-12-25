package com.ezgiyilmaz.sporfinder.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ezgiyilmaz.sporfinder.databinding.ChatsItemBinding
import com.ezgiyilmaz.sporfinder.models.Login
import com.ezgiyilmaz.sporfinder.models.Register

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
    }

    override fun getItemCount(): Int {
        return chatsList.size
    }
}