package com.example.mapsdemo


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mapsdemo.model.BikeStation
import kotlinx.android.synthetic.main.station_list.view.*

class adapterApp  ( private val stationList: List<BikeStation>,
                    private val context: Context) : RecyclerView.Adapter<adapterApp.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): adapterApp.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.station_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = stationList.size

    override fun onBindViewHolder(holder: adapterApp.ViewHolder, position: Int) {
        val listBike = stationList[position]

        holder.number.text = listBike.number.toString()
        holder.name.text = listBike.name
        holder.address.text = listBike.address
        holder.lat.text = listBike.position.lat.toString()
        holder.lng.text = listBike.position.lng.toString()

    }
    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        val number: TextView = itemView.num
        val name: TextView = itemView.name
        val address: TextView = itemView.address
        val lat: TextView = itemView.lat
        val lng: TextView = itemView.lng

    }




}