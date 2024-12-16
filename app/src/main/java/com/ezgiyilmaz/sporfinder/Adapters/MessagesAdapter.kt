package com.ezgiyilmaz.sporfinder.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.ezgiyilmaz.sporfinder.databinding.MessagesItemBinding
import com.ezgiyilmaz.sporfinder.models.Messages

class MessagesAdapter(var messageList: List<Messages>):RecyclerView.Adapter<MessagesAdapter.MesaagesHolder>() {


    class MesaagesHolder(val messagesItemBinding: MessagesItemBinding):RecyclerView.ViewHolder(messagesItemBinding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaagesHolder {
        val binding=MessagesItemBinding.inflate(LayoutInflater.from(parent.context))
        return MesaagesHolder(binding)
    }

    override fun onBindViewHolder(holder: MesaagesHolder, position: Int) {

        holder.messagesItemBinding.editTextText.setText(messageList.get(position).messages)
    }

    override fun getItemCount(): Int {
       return messageList.size
    }
}