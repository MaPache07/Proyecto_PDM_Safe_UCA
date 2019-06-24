package com.mapache.safeuca.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProviders
import com.mapache.safeuca.R
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import kotlinx.android.synthetic.main.activity_new_report.*

class NewReportActivity : AppCompatActivity() {

    private lateinit var reportViewModel : ReportViewModel
    val arrayDanger : Array<String> = arrayOf("Bajo", "Medio", "Alto")
    val arrayType : Array<String> = arrayOf("Reporte", "Mantenimiento")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_report)

        reportViewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)
        //spinner_danger.adapter = ArrayAdapter<String>(this, )
    }
}
