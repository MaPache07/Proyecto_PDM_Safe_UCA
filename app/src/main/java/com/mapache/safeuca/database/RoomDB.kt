package com.mapache.safeuca.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mapache.safeuca.database.daos.ReportDao
import com.mapache.safeuca.database.daos.RolDao
import com.mapache.safeuca.database.daos.UserDao
import com.mapache.safeuca.database.daos.ZoneDao
import com.mapache.safeuca.database.entities.Report
import com.mapache.safeuca.database.entities.Rol
import com.mapache.safeuca.database.entities.User
import com.mapache.safeuca.database.entities.Zone

@Database(entities = arrayOf(Report::class, Zone::class, User::class, Rol::class), version = 1)
public abstract class RoomDB : RoomDatabase() {

    abstract fun reportDao() : ReportDao
    abstract fun zoneDao() : ZoneDao
    abstract fun userDao() : UserDao
    abstract fun rolDao() : RolDao

    companion object{

        @Volatile
        private var INSTANCE : RoomDB? = null

        fun getDatabase(context: Context) : RoomDB{
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance

            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    "Report_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}