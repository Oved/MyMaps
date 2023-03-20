package com.example.mymaps.interfaces

import com.example.mymaps.model.data.dbsqlite.typedata.LocationsGeoJsonSQLite
import com.example.mymaps.model.data.dbsqlite.typedata.Map

interface iView {

    fun showLocations()
    fun showError(message : String)
}