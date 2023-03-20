package com.example.mymaps.interfaces

import com.example.mymaps.model.data.dbsqlite.typedata.LocationsGeoJsonSQLite
import com.example.mymaps.model.data.dbsqlite.typedata.Map

interface iPresenter {

    fun searchData()
    fun showLocations()
    fun showError(message : String)
}