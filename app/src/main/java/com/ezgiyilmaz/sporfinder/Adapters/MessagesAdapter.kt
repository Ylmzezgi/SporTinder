package com.ezgiyilmaz.sporfinder.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ezgiyilmaz.sporfinder.databinding.MessagesItemBinding
import com.ezgiyilmaz.sporfinder.models.Messages

class MessagesAdapter(val messageList: List<Messages>, private val currentUserId: String) :
    RecyclerView.Adapter<MessagesAdapter.MesaagesHolder>() {


    class MesaagesHolder(val messagesItemBinding: MessagesItemBinding) :
        RecyclerView.ViewHolder(messagesItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaagesHolder {
        val binding = MessagesItemBinding.inflate(LayoutInflater.from(parent.context))
        return MesaagesHolder(binding)
    }

    override fun onBindViewHolder(holder: MesaagesHolder, position: Int) {
        val message = messageList[position]
        if (message.sendId == currentUserId) {
            holder.messagesItemBinding.leftChatTextview.visibility = View.GONE
            holder.messagesItemBinding.rightChatTextview.visibility = View.VISIBLE
            holder.messagesItemBinding.rightChatTextview.text=message.messages
        } else {
            holder.messagesItemBinding.leftChatTextview.text = message.messages
            holder.messagesItemBinding.leftChatTextview.visibility = View.VISIBLE
            holder.messagesItemBinding.leftChatTextview.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}