package com.mapache.safeuca.service

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.mapache.safeuca.database.entities.Zone
import com.mapache.safeuca.models.ReportRetro
import com.mapache.safeuca.utilities.AppConstants
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface ReportService {

    @GET("/reports")
    fun getReports() : Deferred<Response<List<ReportRetro>>>

    companion object{
        fun getRetrofit() : ReportService{
            return Retrofit.Builder()
                .baseUrl(AppConstants.REPORT_API_BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build().create(ReportService::class.java)
        }
    }
}