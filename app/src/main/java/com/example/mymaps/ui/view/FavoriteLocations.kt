package com.example.mymaps.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.databinding.ActivityFavoriteLocationsBinding
import com.example.mymaps.model.data.dbsqlite.adminDB.AdminMapsDB
import com.example.mymaps.model.data.dbsqlite.typedata.FavoriteSQLite
import com.example.mymaps.ui.adapter.FavoriteLocationsAdapter
import com.example.mymaps.ui.adapter.FavoriteLocationsAdapter.OnItemClickListener

class FavoriteLocations : AppCompatActivity() {

    private lateinit var bindingFavorites : ActivityFavoriteLocationsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingFavorites = ActivityFavoriteLocationsBinding.inflate(layoutInflater)
        setContentView(bindingFavorites.root)

        val db = AdminMapsDB(this@FavoriteLocations)
        val listFavs = db.showLocations()

        var linear = LinearLayoutManager(this,
            RecyclerView.VERTICAL, false)
        var adapter = FavoriteLocationsAdapter(listFavs, object : OnItemClickListener {
            override fun onItemClick(itemClick: FavoriteSQLite) {
                goToUbication(itemClick)
            }
        })
        bindingFavorites.recyclerFavoriteLocations.adapter = adapter
        bindingFavorites.recyclerFavoriteLocations.layoutManager = linear
        bindingFavorites.recyclerFavoriteLocations.addItemDecoration(DividerItemDecoration(this, linear.getOrientation()))
    }

    fun goToUbication(itemclick : FavoriteSQLite){
        val intent = Intent()
        intent.putExtra("longitude",itemclick.longitude)
        intent.putExtra("latitude", itemclick.latitude)
        setResult(RESULT_OK,intent)
        finish()
    }
}