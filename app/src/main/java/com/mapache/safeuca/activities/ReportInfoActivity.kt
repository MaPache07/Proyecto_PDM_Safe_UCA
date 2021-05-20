package com.mapache.safeuca.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.mapache.safeuca.R
import com.mapache.safeuca.database.entities.Report
import com.mapache.safeuca.database.viewmodels.ReportViewModel
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
        ar_name.text = report.name
        if(report.danger == "0") ar_danger.text = getString(R.string.low)
        else if(report.danger == "1") ar_danger.text = getString(R.string.moderate)
        else if (report.danger == "2") ar_danger.text = getString(R.string.high)

        if(report.type == "0") ar_type.text = getString(R.string.report)
        else ar_type.text = getString(R.string.maintenance)

        ar_description.text = report.description
        reportViewModel.getZone(report.idZone).observe(this, {
            ar_zone.text = it.name
        })
        if(report.status == "0")
            ar_status.text = getText(R.string.pending)
        else ar_status.text = getText(R.string.done)
        ar_user.text = report.mailUser
    }
}
