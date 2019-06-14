package com.mapache.safeuca.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rol_table")
data class Rol (
    @PrimaryKey val id : Int,
    val rol : String
)