package com.mapache.safeuca.database.repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.mapache.safeuca.database.daos.ReportDao
import com.mapache.safeuca.database.daos.RolDao
import com.mapache.safeuca.database.daos.UserDao
import com.mapache.safeuca.database.daos.ZoneDao
import com.mapache.safeuca.database.entities.Report
import com.mapache.safeuca.database.entities.Rol
import com.mapache.safeuca.database.entities.User
import com.mapache.safeuca.database.entities.Zone

class ReportRepository(private val reportDao: ReportDao, private val zoneDao: ZoneDao, private val userDao: UserDao, private val rolDao: RolDao) {

    val allReports : LiveData<List<Report>> = reportDao.getAllReport()
    val allZones : LiveData<List<Zone>> = zoneDao.getAllZone()
    val allUsers : LiveData<List<User>> = userDao.getAllUsers()
    val allRols : LiveData<List<Rol>> = rolDao.getAllRol()

    @WorkerThread
    suspend fun insertReport(report : Report){
        reportDao.insert(report)
    }

    @WorkerThread
    suspend fun insertUser(user : User){
        userDao.insett(user)
    }

    @WorkerThread
    suspend fun insertZone(zone : Zone){
        zoneDao.insert(zone)
    }

    @WorkerThread
    suspend fun insertRol(rol : Rol){
        rolDao.insert(rol)
    }

    fun getReport(id : Int) = reportDao.getReport(id)

    fun getZone(id : Int) = zoneDao.getZone(id)

    fun getUser(mail : String) = userDao.getUser(mail)

    fun getRol(id : Int) = rolDao.getRol(id)

    fun getReportPerUser(mail : String) = reportDao.getReportPerUser(mail)

}