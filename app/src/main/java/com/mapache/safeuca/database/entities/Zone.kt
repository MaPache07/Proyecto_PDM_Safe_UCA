package com.mapache.safeuca.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "zone_table")
data class Zone (
    @PrimaryKey val id : Int,
    val name : String,
    val arrayLat : String,
    val arrayLng : String,
    val edifice : Int,
    val level : Int
)