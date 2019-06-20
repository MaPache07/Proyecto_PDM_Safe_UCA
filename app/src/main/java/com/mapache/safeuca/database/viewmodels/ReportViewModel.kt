package com.mapache.safeuca.database.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.mapache.safeuca.database.RoomDB
import com.mapache.safeuca.database.entities.Report
import com.mapache.safeuca.database.entities.Rol
import com.mapache.safeuca.database.entities.User
import com.mapache.safeuca.database.entities.Zone
import com.mapache.safeuca.database.repositories.ReportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReportViewModel(application: Application) : AndroidViewModel(application) {

    private val repository :ReportRepository
    val allReports : LiveData<List<Report>>
    val allZones : LiveData<List<Zone>>
    val allUsers : LiveData<List<User>>
    val allRols : LiveData<List<Rol>>

    init {
        val reportDao = RoomDB.getDatabase(application).reportDao()
        val zoneDao = RoomDB.getDatabase(application).zoneDao()
        val userDao = RoomDB.getDatabase(application).userDao()
        val rolDao = RoomDB.getDatabase(application).rolDao()

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

    fun getReport(id : Int) = repository.getReport(id)

    fun getZone(id : Int) = repository.getZone(id)

    fun getUser(mail : String) = repository.getUser(mail)

    fun getRol(id : Int) = repository.getRol(id)

    fun getReportPerUser(mail : String) = repository.getReportPerUser(mail)

}