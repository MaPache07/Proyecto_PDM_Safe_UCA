package com.mapache.safeuca.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapache.safeuca.R
import com.mapache.safeuca.adapter.ReportsPerZoneAdapter
import com.mapache.safeuca.database.entities.Zone
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import kotlinx.android.synthetic.main.fragment_zone_info.view.*

class InfoZoneFragment : Fragment() {

    lateinit var zone : Zone
    lateinit var viewF : View
    lateinit var reportAdapter: ReportsPerZoneAdapter
    lateinit var reportViewModel: ReportViewModel

    companion object{
        fun newInstance(zone : Zone) : InfoZoneFragment{
            val newFragment  = InfoZoneFragment()
            newFragment.zone = zone
            return newFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        reportAdapter = ReportsPerZoneAdapter(emptyList(), getString(R.string.pending), getString(R.string.done))
        viewF = inflater.inflate(R.layout.fragment_zone_info, container, false)
        reportViewModel = ViewModelProvider(this).get(ReportViewModel::class.java)
        reportViewModel.getReportsPerZone(zone.id).observe(viewLifecycleOwner, {
            reportAdapter.dataChange(it)
        })
        bindData()
        return viewF
    }

    fun bindData(){
        viewF.zone_info_name.text = zone.name
        viewF.recycler_in_zone.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = reportAdapter
        }
    }

}