package com.example.mymaps.model.data.dbsqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

open class InitDB (context: Context): SQLiteOpenHelper(context, "MapsDB", null, 3){

    val TABLE_FAVORITES = "locations_favorites"
    val TABLE_LOCATIONSGEOJSON = "locations_geojson"
    val TABLE_LOCATIONSGEOJSON_TWO = "locations_geojson_two"

    val createTable = "CREATE TABLE $TABLE_FAVORITES(" +
            "name STRING NOT NULL, " +
            "longitude DOUBLE NOT NULL," +
            "latitude DOUBLE NOT NULL, " +
            " type STRING NOT NULL)";

    val createTableLocations = "CREATE TABLE $TABLE_LOCATIONSGEOJSON(" +
            "longitude DOUBLE NOT NULL," +
            "latitude DOUBLE NOT NULL)";

    val createTableLocationsTwo = "CREATE TABLE $TABLE_LOCATIONSGEOJSON_TWO(" +
            "longitude DOUBLE NOT NULL," +
            "latitude DOUBLE NOT NULL)";

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(createTable)
        db!!.execSQL(createTableLocations)
        db!!.execSQL(createTableLocationsTwo)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITES")
        onCreate(db)
    }
}