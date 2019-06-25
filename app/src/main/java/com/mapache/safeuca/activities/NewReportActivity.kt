package com.mapache.safeuca.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.model.LatLng
import com.mapache.safeuca.R
import com.mapache.safeuca.database.entities.Report
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import com.mapache.safeuca.utilities.AppConstants
import kotlinx.android.synthetic.main.activity_new_report.*

class NewReportActivity : AppCompatActivity() {

    private lateinit var reportViewModel : ReportViewModel
    val arrayDanger : Array<String> = arrayOf("Bajo", "Medio", "Alto")
    val arrayType : Array<String> = arrayOf("Reporte", "Mantenimiento")
    lateinit var dangerSelected : String
    lateinit var typeSelected : String
    lateinit var latLng : LatLng
    lateinit var idZone : String
    var level : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_report)
        reportViewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)

        latLng = intent?.extras?.getParcelable(AppConstants.LATLNT_KEY)!!
        idZone = intent?.extras?.getString(AppConstants.IDZONE_KEY)!!
        level = intent?.extras?.getInt(AppConstants.LEVEL_KEY)!!

        initSpinners()
        setOnClickListeners()
    }

    fun setOnClickListeners(){
        new_report_ok.setOnClickListener {
            if(TextUtils.isEmpty(new_report_name.text) && TextUtils.isEmpty(new_report_description.text)){
                Toast.makeText(applicationContext, "Ingrese todos los datos", Toast.LENGTH_LONG).show()
            } else{
                //val report = Report("", new_report_name.text.trim().toString(), 1, typeSelected, )
            }
        }
    }

    fun initSpinners(){
        spinner_danger.adapter = ArrayAdapter<String>(this, R.layout.simple_spinner_item, R.id.item_spinner, arrayDanger)
        spinner_type.adapter = ArrayAdapter<String>(this, R.layout.simple_spinner_item, R.id.item_spinner, arrayType)

        spinner_danger.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                dangerSelected = parent?.getItemAtPosition(0) as String
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                dangerSelected = parent?.getItemAtPosition(position) as String
            }
        }

        spinner_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                typeSelected = parent?.getItemAtPosition(0) as String
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                typeSelected = parent?.getItemAtPosition(position) as String
            }

        }
    }
}
