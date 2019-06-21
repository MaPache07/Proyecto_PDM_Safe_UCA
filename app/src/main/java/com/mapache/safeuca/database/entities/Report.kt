package com.mapache.safeuca.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "report_table",
        foreignKeys = [ForeignKey(entity = Zone::class, parentColumns = ["id"], childColumns = ["idZone"]),
                    ForeignKey(entity = User::class, parentColumns = ["mail"], childColumns = ["mailUser"])])
data class Report(
    @PrimaryKey
    @field:Json(name = "_id") val id : String,
    @field:Json(name = "name") val name : String,
    @field:Json(name = "danger") val danger : Int,
    @field:Json(name = "type") val type : String,
    @field:Json(name = "status") val status : String,
    @field:Json(name = "mailUser") val mailUser : Int,
    @field:Json(name = "description") val description : String,
    @field:Json(name = "lat") val lat : Double,
    @field:Json(name = "ltn") val ltn : Double,
    @field:Json(name = "idZone") val idZone : String,
    @field:Json(name = "level") val level : Int,
    @field:Json(name = "image") val image : String
)