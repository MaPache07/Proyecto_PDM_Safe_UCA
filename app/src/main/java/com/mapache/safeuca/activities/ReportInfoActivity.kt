package com.mapache.safeuca.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import com.mapache.safeuca.R
import com.mapache.safeuca.database.entities.Report
import com.mapache.safeuca.fragments.InfoReportFragment
import com.mapache.safeuca.utilities.AppConstants.REPORT_KEY
import kotlinx.android.synthetic.main.activity_report_info.*

class ReportInfoActivity : AppCompatActivity() {

    lateinit var infoFragment : InfoReportFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_info)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        FirebaseApp.initializeApp(this@ReportInfoActivity)

        val report : Report? = intent?.extras?.getParcelable(REPORT_KEY)
        infoFragment = InfoReportFragment.newInstance(report!!)
        changeFragment(R.id.content_fragment_info, infoFragment)
    }

    private fun changeFragment(id: Int, frag: Fragment){ supportFragmentManager.beginTransaction().replace(id, frag).commit() }
}
