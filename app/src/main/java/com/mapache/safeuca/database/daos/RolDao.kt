package com.mapache.safeuca.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mapache.safeuca.database.entities.Rol

@Dao
interface RolDao {

    @Insert
    suspend fun insert(rol : Rol)

    @Query("SELECT * FROM rol_table")
    fun getAllRol() : LiveData<List<Rol>>

    @Query("SELECT * FROM rol_table WHERE id = :id")
    fun getRol(id : Int) : Rol

}