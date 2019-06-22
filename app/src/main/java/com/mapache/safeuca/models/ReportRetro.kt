package com.mapache.safeuca.models

import com.mapache.safeuca.database.entities.Zone
import com.squareup.moshi.Json

data class ReportRetro(
    @field:Json(name = "_id") val id : String,
    @field:Json(name = "name") val name : String,
    @field:Json(name = "danger") val danger : Int,
    @field:Json(name = "type") val type : String,
    @field:Json(name = "status") val status : String,
    @field:Json(name = "mailUser") val mailUser : String,
    @field:Json(name = "description") val description : String,
    @field:Json(name = "lat") val lat : Double,
    @field:Json(name = "ltn") val ltn : Double,
    @field:Json(name = "idZone") val idZone : Zone,
    @field:Json(name = "level") val level : Int,
    @field:Json(name = "image") val image : String
)