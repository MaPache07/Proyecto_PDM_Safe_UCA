package com.mapache.safeuca.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "report_table",
        foreignKeys = [ForeignKey(entity = Zone::class, parentColumns = ["id"], childColumns = ["idZone"]),
                    ForeignKey(entity = User::class, parentColumns = ["mail"], childColumns = ["mailUser"])])
data class Report(
    @PrimaryKey val id : Int,
    val name : String,
    val danger : Int,
    val description : String,
    val lat : Double,
    val ltn : Double,
    val idZone : Int,
    val mailUser : Int,
    val image : String,
    val type : String,
    val level : Int,
    val status : String
)