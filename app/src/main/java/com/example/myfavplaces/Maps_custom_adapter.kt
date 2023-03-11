package com.example.myfavplaces

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "Maps_custom_adapter"

class Maps_custom_adapter(val context: MainActivity, val datasource: List<userplace>, private val clickListener: onClickListener) : RecyclerView.Adapter<Maps_custom_adapter.ViewHolder>() {
    interface onClickListener {
        fun onClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_recycler_view,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return datasource.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userplace = datasource[position]
        holder.textid.text = userplace.heading

        holder.itemView.setOnClickListener {
            Log.i(TAG,"Something is Clicked on position: $position")
            //call interface function to get mainactivity notified
            clickListener.onClick(position)
        }
    }

    inner class ViewHolder(itemview: View):RecyclerView.ViewHolder(itemview){
        var textid = itemview.findViewById<TextView>(R.id.text1)
    }
}
