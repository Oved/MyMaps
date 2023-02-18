package com.example.mymaps.interfaces

import com.example.mymaps.model.data.Map

interface iPresenter {

    fun searchData()
    fun showLocations(locations : List<Map>)
    fun showError(message : String)
}