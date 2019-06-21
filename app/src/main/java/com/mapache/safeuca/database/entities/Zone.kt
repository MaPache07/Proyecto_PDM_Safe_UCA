package com.mapache.safeuca.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "zone_table")
data class Zone (
    @PrimaryKey
    @field:Json(name = "_id") val id : String,
    @field:Json(name = "name") val name : String,
    @field:Json(name = "arrayLat") val arrayLat : String,
    @field:Json(name = "arrayLng") val arrayLng : String,
    @field:Json(name = "building") val building : Int,
    @field:Json(name = "level") val level : Int
)