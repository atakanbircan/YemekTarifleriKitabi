package com.yemektariflerikitabi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView

class ListRecyclerAdapter(val yemekList:java.util.ArrayList<String>,val idList:ArrayList<Int>) : RecyclerView.Adapter<ListRecyclerAdapter.YemekHolder>() {

    class YemekHolder(itemView :View): RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YemekHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row,parent,false)
        return YemekHolder(view)
    }

    override fun getItemCount(): Int {
       return yemekList.size
    }

    override fun onBindViewHolder(holder: YemekHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.recycler_row_text).text=yemekList[position]//???
        holder.itemView.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToDescriptionFragment("recylerdangeldim",idList[position])
            Navigation.findNavController(it).navigate(action)
        }
    }


}