package com.example.mymaps.model.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mymaps.model.data.database.dao.MapDao
import com.example.mymaps.model.data.database.entities.MapEntity

@Database(entities = [MapEntity::class], version = 1)
abstract class UbicationDatabase : RoomDatabase() {
    abstract fun getMapDao(): MapDao
}