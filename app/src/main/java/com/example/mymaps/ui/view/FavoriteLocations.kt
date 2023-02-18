package com.example.mymaps.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.databinding.ActivityFavoriteLocationsBinding
import com.example.mymaps.ui.adapter.FavoriteLocationsAdapter

class FavoriteLocations : AppCompatActivity() {

    private lateinit var bindingFavorites : ActivityFavoriteLocationsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingFavorites = ActivityFavoriteLocationsBinding.inflate(layoutInflater)
        setContentView(bindingFavorites.root)

        var linear = LinearLayoutManager(this,
            RecyclerView.VERTICAL, false)
        //var list = savedInstanceState?.get("list_favorites")
        //var adapter = FavoriteLocationsAdapter(list)
        //bindingFavorites.recyclerFavoriteLocations.adapter = adapter
        bindingFavorites.recyclerFavoriteLocations.layoutManager = linear
        bindingFavorites.recyclerFavoriteLocations.addItemDecoration(DividerItemDecoration(this, linear.getOrientation()))

    }
}