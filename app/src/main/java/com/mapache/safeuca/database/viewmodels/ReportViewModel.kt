package com.mapache.safeuca.database.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mapache.safeuca.database.RoomDB
import com.mapache.safeuca.database.entities.Report
import com.mapache.safeuca.database.entities.Rol
import com.mapache.safeuca.database.entities.User
import com.mapache.safeuca.database.entities.Zone
import com.mapache.safeuca.database.repositories.ReportRepository
import com.mapache.safeuca.models.ReportRetro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReportViewModel(private val app: Application) : AndroidViewModel(app) {

    private val repository :ReportRepository
    val allReports : LiveData<List<Report>>
    val allZones : LiveData<List<Zone>>
    val allUsers : LiveData<List<User>>
    val allRols : LiveData<List<Rol>>

    init {
        val reportDao = RoomDB.getDatabase(app).reportDao()
        val zoneDao = RoomDB.getDatabase(app).zoneDao()
        val userDao = RoomDB.getDatabase(app).userDao()
        val rolDao = RoomDB.getDatabase(app).rolDao()

        repository = ReportRepository(reportDao, zoneDao, userDao, rolDao)
        allReports = repository.allReports
        allZones = repository.allZones
        allUsers = repository.allUsers
        allRols = repository.allRols
    }

    fun insertReport(report : Report) = viewModelScope.launch(Dispatchers.IO){
        repository.insertReport(report)
    }

    fun insertZone(zone : Zone) = viewModelScope.launch(Dispatchers.IO){
        repository.insertZone(zone)
    }
    fun insertUser(user : User) = viewModelScope.launch(Dispatchers.IO){
        repository.insertUser(user)
    }

    fun insertRol(rol : Rol) = viewModelScope.launch(Dispatchers.IO){
        repository.insertRol(rol)
    }

    fun getReportsAsync() = viewModelScope.launch {
        this@ReportViewModel.nukeReports()
        this@ReportViewModel.nukeZones()
        val response = repository.getReportsAsync().await()
        if(response.isSuccessful) with(response){
            this.body()?.forEach {
                this@ReportViewModel.insertZone(it.idZone)
                val report = Report(it.id, it.name, it.danger, it.type, it.status, it.mailUser,
                            it.description, it.lat, it.ltn, it.idZone.id, it.level)
                this@ReportViewModel.insertReport(report)
            }
        } else with(response){
            when(this.code()){
                404 -> {
                    Toast.makeText(app, "Reporte no encontrado", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun getZonesAzync() = viewModelScope.launch {
        val response = repository.getZonesAsync().await()
        if(response.isSuccessful) with(response){
            this.body()?.forEach {
                this@ReportViewModel.insertZone(it)
            }
        } else with(response){
            when(this.code()){
                404 -> {
                    Toast.makeText(app, "Zona no encontrada", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun postReport(reportRetro: Report) = viewModelScope.launch(Dispatchers.IO){
        repository.postReport(reportRetro).execute()
        /*val response : Call<DefaultResponse> = repository.postReport(reportRetro)
        if(response.execute().isSuccessful){
            Log.d("Hola", "Funciona")
        }*/
    }

    fun getReport(id : String) = repository.getReport(id)

    fun getReportsPerZone(id : String) = repository.allReportsPerZone(id)

    fun getZone(id : String) = repository.getZone(id)

    fun getUser(mail : String) = repository.getUser(mail)

    fun getRol(id : Int) = repository.getRol(id)

    fun getReportPerUser(mail : String?) = repository.getReportPerUser(mail!!)

    private suspend fun nukeReports() = repository.nukeReports()

    private suspend fun nukeZones() = repository.nukeZones()
}