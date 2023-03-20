package com.example.mymaps.model.data.dbsqlite.adminDB

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.mymaps.model.data.dbsqlite.InitDB
import com.example.mymaps.model.data.dbsqlite.typedata.LocationsGeoJsonSQLite

class AdminMapsGeoJsonDB(val context: Context) : InitDB(context){

    fun insertLocationsGeoJson(longitude : Double, latitude : Double){
        val db = InitDB(context)
        val sqLite = db.writableDatabase
        val values = ContentValues()
        values.put("longitude", longitude)
        values.put("latitude", latitude)

        sqLite.insert(TABLE_LOCATIONSGEOJSON, null, values)
    }

    fun insertLocationsGeoJsonTwo(longitude : Double, latitude : Double){
        val db = InitDB(context)
        val sqLite = db.writableDatabase
        val values = ContentValues()
        values.put("longitude", longitude)
        values.put("latitude", latitude)

        sqLite.insert(TABLE_LOCATIONSGEOJSON_TWO, null, values)
    }

    fun showLocations() : ArrayList<LocationsGeoJsonSQLite>{
        val db = InitDB(context)
        val sqLite = db.writableDatabase

        val listGeoJson = ArrayList<LocationsGeoJsonSQLite>()
        var location : LocationsGeoJsonSQLite
        val cursor : Cursor

        cursor = sqLite.rawQuery("SELECT * FROM $TABLE_LOCATIONSGEOJSON", null)
        if (cursor.moveToFirst()){
            do {
                location = LocationsGeoJsonSQLite(
                    cursor.getDouble(0),
                    cursor.getDouble(1))
                listGeoJson.add(location)
            }while (cursor.moveToNext())
        }
        cursor.close()
        return listGeoJson
    }

    fun showLocationsTwo() : ArrayList<LocationsGeoJsonSQLite>{
        val db = InitDB(context)
        val sqLite = db.writableDatabase

        val listGeoJson = ArrayList<LocationsGeoJsonSQLite>()
        var location : LocationsGeoJsonSQLite
        val cursor : Cursor

        cursor = sqLite.rawQuery("SELECT * FROM $TABLE_LOCATIONSGEOJSON_TWO", null)
        if (cursor.moveToFirst()){
            do {
                location = LocationsGeoJsonSQLite(
                    cursor.getDouble(0),
                    cursor.getDouble(1))
                listGeoJson.add(location)
            }while (cursor.moveToNext())
        }
        cursor.close()
        return listGeoJson
    }
}