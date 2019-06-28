package com.mapache.safeuca.database.repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.mapache.safeuca.database.daos.ReportDao
import com.mapache.safeuca.database.daos.ZoneDao
import com.mapache.safeuca.database.entities.Report
import com.mapache.safeuca.database.entities.Zone
import com.mapache.safeuca.models.DefaultResponse
import com.mapache.safeuca.models.ReportRetro
import com.mapache.safeuca.service.ReportService
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response

class ReportRepository(private val reportDao: ReportDao, private val zoneDao: ZoneDao) {

    val allReports : LiveData<List<Report>> = reportDao.getAllReport()
    val allZones : LiveData<List<Zone>> = zoneDao.getAllZone()

    @WorkerThread
    suspend fun insertReport(report : Report){
        reportDao.insert(report)
    }

    @WorkerThread
    suspend fun insertZone(zone : Zone){
        zoneDao.insert(zone)
    }

    fun allReportsPerZone(idZone : String)  = reportDao.getReportPerZone(idZone)

    fun getReport(id : String) = reportDao.getReport(id)

    fun getZone(id : String) = zoneDao.getZone(id)

    fun getReportPerUser(mail : String) = reportDao.getReportPerUser(mail)

    fun getReportsAsync() : Deferred<Response<List<ReportRetro>>> {
        return ReportService.getRetrofit().getReports()
    }

    fun getZonesAsync() : Deferred<Response<List<Zone>>> {
        return  ReportService.getRetrofit().getZones()
    }

    fun postReport(reportRetro: Report) : Call<DefaultResponse> {
        return ReportService.getRetrofit().postReport(
            reportRetro.name,
            reportRetro.danger,
            reportRetro.type,
            reportRetro.status,
            reportRetro.mailUser,
            reportRetro.description,
            reportRetro.lat,
            reportRetro.ltn,
            reportRetro.idZone,
            reportRetro.level
        )
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