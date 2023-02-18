package com.example.mymaps.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.R
import com.example.mymaps.databinding.CardViewFavLocationsBinding
import com.mapbox.geojson.Point

class FavoriteLocationsAdapter(list: Any?) : RecyclerView.Adapter<FavoriteLocationsAdapter.LocationHolder>() {

    var listLocations : ArrayList<Point> = list as ArrayList<Point>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_fav_locations, parent, false)
        return LocationHolder(view)
    }

    override fun onBindViewHolder(holder: LocationHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount() = listLocations.size

    inner class LocationHolder(view : View) : RecyclerView.ViewHolder(view) {
        val bindingHolder = CardViewFavLocationsBinding.bind(view)

        fun onBind(position: Int){
            bindingHolder.tvLatitude.text = listLocations[position].latitude().toString()
            bindingHolder.tvLongitude.text = listLocations[position].longitude().toString()
            bindingHolder.tvLocation.text = listLocations[position].type()
        }
    }
}