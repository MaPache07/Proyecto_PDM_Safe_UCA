package com.mapache.safeuca.database.entities

import android.os.Parcel
import android.os.Parcelable
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(name)
        dest.writeString(arrayLat)
        dest.writeString(arrayLng)
        dest.writeInt(building)
        dest.writeInt(level)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Zone> {
        override fun createFromParcel(parcel: Parcel): Zone {
            return Zone(parcel)
        }

        override fun newArray(size: Int): Array<Zone?> {
            return arrayOfNulls(size)
        }
    }
}