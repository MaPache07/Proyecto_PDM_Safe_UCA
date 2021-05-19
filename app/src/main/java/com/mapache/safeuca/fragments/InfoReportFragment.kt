package com.mapache.safeuca.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mapache.safeuca.R
import com.mapache.safeuca.database.entities.Report
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import kotlinx.android.synthetic.main.fragment_info_report.view.*

class InfoReportFragment : Fragment(){

    lateinit var report : Report
    lateinit var viewF : View
    private lateinit var reportViewModel : ReportViewModel

    companion object{
        fun newInstance(report : Report) : InfoReportFragment{
            val newFragment = InfoReportFragment()
            newFragment.report = report
            return newFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        viewF = inflater.inflate(R.layout.fragment_info_report, container, false)
        reportViewModel = ViewModelProvider(this).get(ReportViewModel::class.java)
        bindData()
        return viewF
    }

    fun bindData(){
        viewF.ar_name.text = report.name
        if(report.danger == "0") viewF.ar_danger.text = getString(R.string.low)
        else if(report.danger == "1") viewF.ar_danger.text = getString(R.string.moderate)
        else if (report.danger == "2") viewF.ar_danger.text = getString(R.string.high)

        if(report.type == "0") viewF.ar_type.text = getString(R.string.report)
        else viewF.ar_type.text = getString(R.string.maintenance)

        viewF.ar_description.text = report.description
        reportViewModel.getZone(report.idZone).observe(viewLifecycleOwner, {
            viewF.ar_zone.text = it.name
        })
        if(report.status == "0")
            viewF.ar_status.text = getText(R.string.pending)
        else viewF.ar_status.text = getText(R.string.done)
        viewF.ar_user.text = report.mailUser
    }
}