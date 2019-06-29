package com.mapache.safeuca.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.mapache.safeuca.R
import com.mapache.safeuca.database.entities.Report
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
    lateinit var arrayDanger : Array<String>
    lateinit var arrayType : Array<String>
    private lateinit var auth : FirebaseAuth
    lateinit var dangerSelected : String
    lateinit var typeSelected : String
    lateinit var latLng : LatLng
    lateinit var idZone : String
    var level : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_report)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        FirebaseApp.initializeApp(this@NewReportActivity)

        reportViewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)
        auth = FirebaseAuth.getInstance()

        arrayDanger = arrayOf(getString(R.string.low), getString(R.string.moderate), getString(R.string.high))
        arrayType = arrayOf(getString(R.string.report), getString(R.string.maintenance))

        latLng = intent?.extras?.getParcelable(AppConstants.LATLNT_KEY)!!
        idZone = intent?.extras?.getString(AppConstants.ZONE_KEY)!!
        level = intent?.extras?.getInt(AppConstants.LEVEL_KEY)!!

        initSpinners()
        setOnClickListeners()
    }

    fun setOnClickListeners(){
        new_report_ok.setOnClickListener {
            if(TextUtils.isEmpty(new_report_name.text) && TextUtils.isEmpty(new_report_description.text)){
                Toast.makeText(applicationContext, getString(R.string.all_data), Toast.LENGTH_LONG).show()
            } else{
                val retroRepo = Report("", new_report_name.text.trim().toString(), dangerSelected, typeSelected,
                    "0", auth.currentUser?.email!!, new_report_description.text.trim().toString(),
                    latLng.latitude, latLng.longitude, idZone, level)
                if(checkNetworkStatus()) reportViewModel.postReport(retroRepo)
                else Toast.makeText(applicationContext, getString(R.string.internet_required), Toast.LENGTH_LONG).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
        new_report_cancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
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

    fun checkNetworkStatus() : Boolean{
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        return if(connectivityManager is ConnectivityManager){
            val networkInfo : NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }
}
