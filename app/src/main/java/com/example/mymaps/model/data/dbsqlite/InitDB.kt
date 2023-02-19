package com.example.mymaps.model.data.dbsqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

open class InitDB (context: Context): SQLiteOpenHelper(context, "MapsDB", null, 1){

    val TABLE_FAVORITES = "locations_favorites"

    val createTable = "CREATE TABLE $TABLE_FAVORITES(" +
            "name STRING NOT NULL, " +
            "longitude DOUBLE NOT NULL," +
            " latitude DOUBLE NOT NULL)";

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(createTable )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}