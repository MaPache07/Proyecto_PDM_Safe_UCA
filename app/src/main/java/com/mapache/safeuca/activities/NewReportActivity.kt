package com.mapache.safeuca.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProviders
import com.mapache.safeuca.R
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import kotlinx.android.synthetic.main.activity_new_report.*

class NewReportActivity : AppCompatActivity() {

    private lateinit var reportViewModel : ReportViewModel
    val arrayDanger : Array<String> = arrayOf("Bajo", "Medio", "Alto")
    val arrayType : Array<String> = arrayOf("Reporte", "Mantenimiento")
    lateinit var dangerSelected : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_report)
        reportViewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)
        initSpinners()
    }

    fun initSpinners(){
        spinner_danger.adapter = ArrayAdapter<String>(this, R.layout.simple_spinner_item, R.id.item_spinner, arrayDanger)
        spinner_type.adapter = ArrayAdapter<String>(this, R.layout.simple_spinner_item, R.id.item_spinner, arrayType)

        spinner_danger.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                dangerSelected = parent?.getItemAtPosition(position) as String
            }

        }
    }
}
