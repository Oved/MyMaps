package com.example.mymaps.model.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mymaps.model.data.database.entities.MapEntity

@Dao
interface MapDao {

    @Query("SELECT * FROM map_table")
    suspend fun getMaps():List<MapEntity>

    @Insert
    suspend fun insertMap(maps:List<MapEntity>)
}