package com.example.myfavplaces

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "Maps_custom_adapter"

class Maps_custom_adapter(val context: MainActivity, val datasource: List<userplace>, private val clickListener: Maps_custom_adapter.onClickListener) : RecyclerView.Adapter<Maps_custom_adapter.ViewHolder>() {
    interface onClickListener {
        fun onClick(position: Int)
        fun onlongClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_recycler_view,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return datasource.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cuserplace = datasource[position]
        holder.headingid.text = cuserplace.heading
        val innertitles = java.lang.StringBuilder("")
        for(place in cuserplace.places){
            innertitles.append(place.title)
            innertitles.append("\n")
        }
        holder.titlesid.text = innertitles.toString()

        holder.itemView.setOnClickListener {
            Log.i(TAG,"Something is Clicked on position: $position")
            //call interface function to get mainactivity notified
            clickListener.onClick(position)
        }

        holder.itemView.setOnLongClickListener{
            Log.i(TAG,"Something is long clicked on positon: $position")
            clickListener.onlongClick(position)
            return@setOnLongClickListener true
        }
    }

    inner class ViewHolder(itemview: View):RecyclerView.ViewHolder(itemview){
        var headingid = itemview.findViewById<TextView>(R.id.heading)
        var titlesid = itemview.findViewById<TextView>(R.id.innerTitle)
    }
}
