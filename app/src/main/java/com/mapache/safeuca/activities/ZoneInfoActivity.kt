package com.mapache.safeuca.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mapache.safeuca.R
import com.mapache.safeuca.adapter.ReportsPerZoneAdapter
import com.mapache.safeuca.database.entities.Report
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import kotlinx.android.synthetic.main.activity_zone_info.*

class ZoneInfoActivity : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: ReportsPerZoneAdapter
    private lateinit var RepByZoneViewModel : ReportViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zone_info)

        RepByZoneViewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)

        val intento  = intent
        var name = intento.getStringExtra("name")
        var idZone = intento.getStringExtra("idZone")

        if(intento != null){
            RepByZoneViewModel.getReportsPerZone(idZone).observe(this, Observer { match ->
                viewAdapter.dataChange(match)
            })
        }
        zone_info_name.text = name
        initRecycler(emptyList())
    }
    fun initRecycler(report : List<Report>){
        viewManager = LinearLayoutManager(this)
        viewAdapter = ReportsPerZoneAdapter(report)
        recycler_in_zone.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}
