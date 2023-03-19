package com.example.mymaps.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.R
import com.example.mymaps.databinding.CardViewFavLocationsBinding
import com.example.mymaps.model.data.dbsqlite.typedata.FavoriteSQLite

class FavoriteLocationsAdapter(list: List<FavoriteSQLite>, clickFav : FavoriteLocationsAdapter.OnItemClickListener) : RecyclerView.Adapter<FavoriteLocationsAdapter.LocationHolder>() {

    var listLocations = list
     var click : FavoriteLocationsAdapter.OnItemClickListener = clickFav

    interface OnItemClickListener{
        fun onItemClick(fav : FavoriteSQLite)
    }

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
            bindingHolder.tvLatitude.text = listLocations[position].latitude.toString()
            bindingHolder.tvLongitude.text = listLocations[position].longitude.toString()
            bindingHolder.tvLocation.text = listLocations[position].nameLocation
            if (listLocations[position].type == "Alert"){
                bindingHolder.itemLocation.setImageDrawable(bindingHolder.itemLocation.resources.getDrawable(R.drawable.ic_baseline_report_24))
            }else{
                bindingHolder.itemLocation.setImageDrawable(bindingHolder.itemLocation.resources.getDrawable(R.drawable.ic_baseline_location_on_24))
            }
            bindingHolder.buttonGo.setOnClickListener { click.onItemClick(listLocations[position]) }
        }
    }
}