package com.mapache.safeuca.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.auth.FirebaseAuth
import com.mapache.safeuca.R
import com.mapache.safeuca.activities.ReportInfoActivity
import com.mapache.safeuca.adapter.ReportAdapter
import com.mapache.safeuca.database.entities.Report
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import com.mapache.safeuca.utilities.AppConstants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_reports.*
import kotlinx.android.synthetic.main.fragment_reports.view.*

class ReportsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var reportViewModel : ReportViewModel
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: ReportAdapter
    private lateinit var flag : TextView

    companion object{
        fun newInstance (): ReportsFragment {
            val newFragment1 = ReportsFragment()
            return  newFragment1
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_reports,container,false)
        auth = FirebaseAuth.getInstance()
        flag = activity!!.findViewById(R.id.tv_escondido2)
        reportViewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)
        if(flag.text == "0"){
            reportViewModel.getReportPerUser(auth.currentUser!!.email).observe(this, Observer { match ->
                viewAdapter.dataChange(match)
            })
        }
        else{
            reportViewModel.allReports.observe(this, Observer { match ->
                viewAdapter.dataChange(match)
            })
        }
        initRecycler(emptyList(), view)
        return view
    }

    fun initRecycler(match : List<Report>, view: View){
        viewManager = LinearLayoutManager(context)
        viewAdapter = ReportAdapter(match,{ matchItem: Report-> onClicked(matchItem)})
        view.recycler.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    fun onClicked(item : Report){
        val extras = Bundle()
        extras.putString("name",item.name)
        extras.putInt("danger",item.danger)
        extras.putString("type",item.type)
        extras.putString("status",item.status)
        extras.putString("mail",item.mailUser)
        extras.putString("desc",item.description)
        extras.putDouble("lat",item.lat)
        extras.putDouble("long",item.ltn)
        extras.putInt("level",item.level)
        extras.putString("img",item.image)

        startActivity(Intent(context, ReportInfoActivity::class.java).putExtras(extras))
    }
}