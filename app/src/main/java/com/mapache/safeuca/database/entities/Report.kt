package com.mapache.safeuca.database.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "report_table",
        foreignKeys = [ForeignKey(entity = Zone::class, parentColumns = ["id"], childColumns = ["idZone"])])
data class Report(
        @PrimaryKey
    @field:Json(name = "_id") val id : String,
    @field:Json(name = "name") val name : String,
    @field:Json(name = "danger") val danger : String,
    @field:Json(name = "type") val type : String,
    @field:Json(name = "status") var status : String,
    @field:Json(name = "mailUser") val mailUser : String,
    @field:Json(name = "image") val image : String,
    @field:Json(name = "description") val description : String,
    @field:Json(name = "lat") val lat : Double,
    @field:Json(name = "ltn") val ltn : Double,
    @field:Json(name = "idZone") val idZone : String,
    @field:Json(name = "level") val level : Int
) : Parcelable {
    constructor(parcel : Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(name)
        dest.writeString(danger)
        dest.writeString(type)
        dest.writeString(status)
        dest.writeString(mailUser)
        dest.writeString(image)
        dest.writeString(description)
        dest.writeDouble(lat)
        dest.writeDouble(ltn)
        dest.writeString(idZone)
        dest.writeInt(level)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Report> {
        override fun createFromParcel(parcel: Parcel): Report {
            return Report(parcel)
        }

        override fun newArray(size: Int): Array<Report?> {
            return arrayOfNulls(size)
        }
    }
}