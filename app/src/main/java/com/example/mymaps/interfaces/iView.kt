package com.example.mymaps.interfaces

import com.example.mymaps.model.data.dbsqlite.typedata.Map

interface iView {

    fun showLocations(locations : List<Map>)
    fun showError(message : String)
}