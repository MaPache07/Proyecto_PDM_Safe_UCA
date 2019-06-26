package com.mapache.safeuca.models

import com.mapache.safeuca.database.entities.Report

data class DefaultResponse(
    val message : String,
    val createdReport : Report
)