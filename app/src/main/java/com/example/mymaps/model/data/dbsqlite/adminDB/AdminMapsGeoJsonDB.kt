package com.example.mymaps.model.data.dbsqlite.adminDB

import android.content.ContentValues
import android.content.Context
import com.example.mymaps.model.data.dbsqlite.InitDB

class AdminMapsGeoJsonDB(val context: Context) : InitDB(context){

    fun insertMapsGeoJson(name : String, longitude : Double, latitude : Double){
        val db = InitDB(context)
        val sqLite = db.writableDatabase
        val values = ContentValues()
        values.put("name", name)
        values.put("longitude", longitude)
        values.put("latitude", latitude)

        sqLite.insert(TABLE_FAVORITES, null, values)
    }
}