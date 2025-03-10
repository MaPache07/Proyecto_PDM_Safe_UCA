package com.mapache.safeuca.activities

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.mapache.safeuca.R
import com.mapache.safeuca.database.entities.Report
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import com.mapache.safeuca.utilities.AppConstants.ADMIN_FLAG
import com.mapache.safeuca.utilities.AppConstants.REPORT_KEY
import kotlinx.android.synthetic.main.activity_report_info.*

class ReportInfoActivity : AppCompatActivity() {

    lateinit var report : Report
    private lateinit var reportViewModel : ReportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_info)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        FirebaseApp.initializeApp(this@ReportInfoActivity)

        report = intent?.extras?.getParcelable(REPORT_KEY)!!
        reportViewModel = ViewModelProvider(this).get(ReportViewModel::class.java)

        bindData()
    }

    fun bindData(){
        val imageBytes = Base64.decode(report.image, 0)
        ar_image.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size))
        ar_name.text = report.name
        if(report.danger == "0") ar_danger.text = getString(R.string.low)
        else if(report.danger == "1") ar_danger.text = getString(R.string.moderate)
        else if (report.danger == "2") ar_danger.text = getString(R.string.high)

        if(report.type == "0") ar_type.text = getString(R.string.report)
        else if(report.type == "1") ar_type.text = getString(R.string.maintenance)
        else if(report.type == "2") ar_type.text = getString(R.string.desinfection)

        ar_description.text = report.description
        reportViewModel.getZone(report.idZone).observe(this, {
            ar_zone.text = it.name
        })
        if(report.status == "0")
            ar_status.text = getText(R.string.pending)
        else ar_status.text = getText(R.string.done)
        ar_user.text = report.mailUser

        if(report.status != "0"){
            report_accept.visibility = View.INVISIBLE
            report_reject.visibility = View.INVISIBLE
        }

        setOnClickListeners()
    }

    fun checkNetworkStatus() : Boolean{
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        return if(connectivityManager is ConnectivityManager){
            val networkInfo : NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    fun setOnClickListeners(){
        report_accept.setOnClickListener {
            if(checkNetworkStatus()){
                report.status = "1"
                reportViewModel.putReport(report)
            }
            else Toast.makeText(applicationContext, getString(R.string.internet_required), Toast.LENGTH_LONG).show()
        }
        report_reject.setOnClickListener {
            if(checkNetworkStatus()){
                report.status = "2"
                reportViewModel.putReport(report)
            }
            else Toast.makeText(applicationContext, getString(R.string.internet_required), Toast.LENGTH_LONG).show()
        }
    }
}
