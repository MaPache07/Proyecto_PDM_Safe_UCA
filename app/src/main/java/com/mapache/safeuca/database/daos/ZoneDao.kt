package com.mapache.safeuca.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mapache.safeuca.database.entities.Zone

@Dao
interface ZoneDao {

    @Insert
    suspend fun insert(zone : Zone)

    @Query("SELECT * FROM zone_table WHERE id = :id")
    fun getZone(id : Int) : Zone

    @Query("SELECT * FROM zone_table")
    fun getAllZone() : LiveData<List<Zone>>
}