package com.example.mymaps.ui.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mymaps.databinding.ActivityMainBinding
import com.example.mymaps.interfaces.iPresenter
import com.example.mymaps.interfaces.iView
import com.example.mymaps.presenter.PresenterDataImpl
import com.mapbox.maps.MAPBOX_ACCESS_TOKEN_RESOURCE_NAME
import com.mapbox.maps.Style

@SuppressLint("Lifecycle")
class MainView : AppCompatActivity(), iView {

    private lateinit var binding: ActivityMainBinding
    private lateinit var presenter: iPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        MAPBOX_ACCESS_TOKEN_RESOURCE_NAME
        setContentView(binding.root)
        binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
        presenter = PresenterDataImpl(this)
        searchData()

    }

    private fun searchData(){
        presenter.searchData()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }


    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }
}