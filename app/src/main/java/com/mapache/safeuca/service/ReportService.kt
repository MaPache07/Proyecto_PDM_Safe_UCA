package com.mapache.safeuca.service

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.mapache.safeuca.database.entities.Zone
import com.mapache.safeuca.models.DefaultResponse
import com.mapache.safeuca.models.ReportRetro
import com.mapache.safeuca.utilities.AppConstants
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface ReportService {

    @GET("/reports")
    fun getReports() : Deferred<Response<List<ReportRetro>>>

    @GET("/zones")
    fun getZones() : Deferred<Response<List<Zone>>>

    @FormUrlEncoded
    @POST("/reports")
    fun postReport(
        //@Body body : String
        @Field("name") name : String,
        @Field("danger") danger : String,
        @Field("type") type : String,
        @Field("status") status : String,
        @Field("mailUser") mailUser : String,
        @Field("description") description : String,
        @Field("lat") lat : Double,
        @Field("ltn") ltn : Double,
        @Field("idZone") idZone : String,
        @Field("level") level : Int
    ) : Call<DefaultResponse>

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