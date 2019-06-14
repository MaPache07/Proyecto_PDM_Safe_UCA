package com.mapache.safeuca.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "user_table",
        foreignKeys = [ForeignKey(entity = Rol::class, parentColumns = ["id"], childColumns = ["idRol"])])
data class User (
    val name : String,
    val password : String,
    @PrimaryKey val mail : String,
    val gender : String,
    val idRol : Int
)