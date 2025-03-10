package com.mapache.safeuca.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mapache.safeuca.database.entities.Report
import com.mapache.safeuca.database.entities.Zone

@Dao
interface ReportDao {

    @Insert
    suspend fun insert(report : Report)

    @Update
    suspend fun update(report : Report)

    @Query("SELECT * FROM report_table")
    fun getAllReport() : LiveData<List<Report>>

    @Query("SELECT * FROM report_table WHERE id = :id")
    fun getReport(id : String) : Report

    @Query("SELECT * FROM report_table WHERE mailUser = :mail")
    fun getReportPerUser(mail : String) : LiveData<List<Report>>

    @Query("SELECT * FROM report_table WHERE idZone = :idZone")
    fun getReportPerZone(idZone : String) : LiveData<List<Report>>

    @Query("DELETE FROM report_table")
    suspend fun nukeTable()
}