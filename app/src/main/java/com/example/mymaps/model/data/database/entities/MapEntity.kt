package com.example.mymaps.model.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "map_table")
data class MapEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "scalerank")
    var scalerank: Int,
    @ColumnInfo(name = "natscale")
    var natscale: Int,
    @ColumnInfo(name = "labelrank")
    var labelrank: Int,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "namepar")
    var namepar: String,
    @ColumnInfo(name = "namealt")
    var namealt: String,
    @ColumnInfo(name = "diffascii")
    var diffascii: Int,
    @ColumnInfo(name = "nameascii")
    var nameascii: String,
    @ColumnInfo(name = "adm0cap")
    var adm0cap: Int,
    @ColumnInfo(name = "capalt")
    var capalt: Int,
    @ColumnInfo(name = "capin")
    var capin: String,
    @ColumnInfo(name = "worldcity")
    var worldcity: Int,
    @ColumnInfo(name = "megacity")
    var megacity: Int,
    @ColumnInfo(name = "sov0name")
    var sov0name: String,
    @ColumnInfo(name = "sov_a3")
    var sov_a3: String,
    @ColumnInfo(name = "adm0name")
    var adm0name: String,
    @ColumnInfo(name = "adm0_a3")
    var adm0_a3: String,
    @ColumnInfo(name = "adm1name")
    var adm1name: String,
    @ColumnInfo(name = "iso_a2")
    var iso_a2: String,
    @ColumnInfo(name = "note")
    var note: String,
    @ColumnInfo(name = "latitude")
    var latitude: Double,
    @ColumnInfo(name = "longitude")
    var longitude: Double,
    @ColumnInfo(name = "changed")
    var changed: Int,
    @ColumnInfo(name = "namediff")
    var namediff: Int,
    @ColumnInfo(name = "diffnote")
    var diffnote: String,
    @ColumnInfo(name = "pop_max")
    var pop_max: Int,
    @ColumnInfo(name = "pop_min")
    var pop_min: Int,
    @ColumnInfo(name = "pop_other")
    var pop_other: Int,
    @ColumnInfo(name = "rank_max")
    var rank_max: Int,
    @ColumnInfo(name = "rank_min")
    var rank_min: Int,
    @ColumnInfo(name = "geonameid")
    var geonameid: Int,
    @ColumnInfo(name = "meganame")
    var meganame: String,
    @ColumnInfo(name = "ls_name")
    var ls_name: String,
    @ColumnInfo(name = "ls_match")
    var ls_match: Int,
    @ColumnInfo(name = "checkme")
    var checkme: Int,
    @ColumnInfo(name = "featureclass")
    var featureclass: String
)