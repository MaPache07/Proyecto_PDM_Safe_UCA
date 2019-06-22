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
import com.mapache.safeuca.models.ReportRetro
import com.mapache.safeuca.service.ReportService
import kotlinx.coroutines.Deferred
import retrofit2.Response

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

    fun getReport(id : String) = reportDao.getReport(id)

    fun getZone(id : String) = zoneDao.getZone(id)

    fun getUser(mail : String) = userDao.getUser(mail)

    fun getRol(id : Int) = rolDao.getRol(id)

    fun getReportPerUser(mail : String) = reportDao.getReportPerUser(mail)

    fun getReportsAsync() : Deferred<Response<List<ReportRetro>>> {
        return ReportService.getRetrofit().getReports()
    }

    fun getZonesAsync() : Deferred<Response<List<Zone>>> {
        return  ReportService.getRetrofit().getZones()
    }

    @WorkerThread
    suspend fun nukeReports(){
        return reportDao.nukeTable()
    }

    @WorkerThread
    suspend fun nukeZones(){
        return zoneDao.nukeTable()
    }

}