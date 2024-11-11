package com.ezgiyilmaz.sporfinder.viewModel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ListitemBinding
import com.ezgiyilmaz.sporfinder.models.GetPlayerModel
import com.ezgiyilmaz.sporfinder.models.GetRivalModel
import java.text.SimpleDateFormat
import java.util.Locale

class HomePageAdapter( val matchList:List<Any>, val selected:String):RecyclerView.Adapter<HomePageAdapter.HomeViewHolder>(){
    class HomeViewHolder(val binding: ListitemBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding=ListitemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        var category=""

        if(selected=="rival"){
            matchList as List<GetRivalModel>
            category=matchList[position].category
            holder.binding.playItemTextview.text=matchList[position].category
            holder.binding.locationItemTextView.text=matchList[position].city
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val formattedDate = dateFormat.format(matchList[position].dateTime.toDate())
            holder.binding.dateItemTextView.text = formattedDate
        }else{
            matchList as List<GetPlayerModel>
            category=matchList[position].category
            holder.binding.playItemTextview.text=matchList[position].category
            holder.binding.locationItemTextView.text=matchList[position].city
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val formattedDate = dateFormat.format(matchList[position].dateTime!!.toDate())
            holder.binding.dateItemTextView.text = formattedDate

        }
        if(category=="Futbol"){
            holder.binding.imageView2.setImageResource(R.drawable.football)
        }
        if(category=="Basketbol"){
            holder.binding.imageView2.setImageResource(R.drawable.basketball)
        }

        if(category=="Tenis"){
            holder.binding.imageView2.setImageResource(R.drawable.tennis)
        }

        if(category=="Voleybol"){
            holder.binding.imageView2.setImageResource(R.drawable.volleyball)
        }



    }

    override fun getItemCount(): Int {
        return matchList.size
    }

}