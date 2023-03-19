package com.example.mymaps.model.data.dbsqlite.adminDB

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.mymaps.model.data.dbsqlite.InitDB
import com.example.mymaps.model.data.dbsqlite.typedata.FavoriteSQLite

class AdminMapsDB(val context: Context) : InitDB(context){

    fun insertLocation(name : String, longitude : Double, latitude : Double, typePoint : String){
        val db = InitDB(context)
        val sqLite = db.writableDatabase
        val values = ContentValues()
        values.put("name", name)
        values.put("longitude", longitude)
        values.put("latitude", latitude)
        values.put("type", typePoint)

        sqLite.insert(TABLE_FAVORITES, null, values)
    }

    fun showLocations() : ArrayList<FavoriteSQLite>{
        val db = InitDB(context)
        val sqLite = db.writableDatabase

        val listFavs = ArrayList<FavoriteSQLite>()
        var location : FavoriteSQLite
        val cursor : Cursor

        cursor = sqLite.rawQuery("SELECT * FROM $TABLE_FAVORITES", null)
        if (cursor.moveToFirst()){
            do {
                location = FavoriteSQLite(
                    cursor.getString(0),
                    cursor.getDouble(1),
                    cursor.getDouble(2),
                    cursor.getString(3))
                listFavs.add(location)
            }while (cursor.moveToNext())
        }
        cursor.close()
        return listFavs
    }
}