package com.mapache.safeuca.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mapache.safeuca.database.entities.Rol
import com.mapache.safeuca.database.entities.User

@Dao
interface UserDao {

    @Insert
    suspend fun insett(user : User)

    @Query("SELECT * FROM user_table")
    fun getAllUsers() : LiveData<List<User>>

    @Query("SELECT * FROM user_table WHERE mail = :mail")
    fun getUser(mail : String) : User
}