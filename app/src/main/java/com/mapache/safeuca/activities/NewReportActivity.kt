package com.mapache.safeuca.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.mapache.safeuca.R
import com.mapache.safeuca.database.entities.Zone
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import com.mapache.safeuca.models.DefaultResponse
import com.mapache.safeuca.models.ReportRetro
import com.mapache.safeuca.utilities.AppConstants
import kotlinx.android.synthetic.main.activity_new_report.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewReportActivity : AppCompatActivity() {

    private lateinit var reportViewModel : ReportViewModel
    val arrayDanger : Array<String> = arrayOf("Bajo", "Medio", "Alto")
    val arrayType : Array<String> = arrayOf("Reporte", "Mantenimiento")
    private lateinit var auth: FirebaseAuth
    lateinit var dangerSelected : String
    lateinit var typeSelected : String
    lateinit var latLng : LatLng
    lateinit var zone : Zone
    var level : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_report)
        reportViewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)
        auth = FirebaseAuth.getInstance()

        latLng = intent?.extras?.getParcelable(AppConstants.LATLNT_KEY)!!
        zone = intent?.extras?.getParcelable(AppConstants.ZONE_KEY)!!
        level = intent?.extras?.getInt(AppConstants.LEVEL_KEY)!!

        initSpinners()
        setOnClickListeners()
    }

    fun setOnClickListeners(){
        new_report_ok.setOnClickListener {
            if(TextUtils.isEmpty(new_report_name.text) && TextUtils.isEmpty(new_report_description.text)){
                Toast.makeText(applicationContext, "Ingrese todos los datos", Toast.LENGTH_LONG).show()
            } else{
                val retroRepo = ReportRetro("", new_report_name.text.trim().toString(), dangerSelected, typeSelected,
                    "No Resuelto", auth.currentUser?.email!!, new_report_description.text.trim().toString(),
                    latLng.latitude, latLng.longitude, zone, level)
                reportViewModel.postReport(retroRepo).enqueue(object : Callback<DefaultResponse>{
                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "Ocurrio un error", Toast.LENGTH_LONG).show()
                    }
                    override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                        Toast.makeText(applicationContext, "Se reporto correctamente", Toast.LENGTH_LONG).show()
                    }
                })
                finish()
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
