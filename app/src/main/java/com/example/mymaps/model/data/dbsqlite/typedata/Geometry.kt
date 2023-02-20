package com.example.mymaps.model.data.dbsqlite.typedata

data class Geometry(
    var type : String,
    var coordinates : ArrayList<Double>
)