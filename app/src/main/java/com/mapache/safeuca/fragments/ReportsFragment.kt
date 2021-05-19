package com.mapache.safeuca.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mapache.safeuca.R
import com.mapache.safeuca.activities.ReportInfoActivity
import com.mapache.safeuca.adapter.ReportAdapter
import com.mapache.safeuca.database.entities.Report
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import com.mapache.safeuca.utilities.AppConstants
import com.mapache.safeuca.utilities.AppConstants.REPORT_KEY
import kotlinx.android.synthetic.main.fragment_reports.view.*

class ReportsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var reportViewModel : ReportViewModel
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: ReportAdapter
    lateinit var pref : SharedPreferences
    lateinit var viewF : View

    companion object{
        fun newInstance (): ReportsFragment {
            val newFragment1 = ReportsFragment()
            return  newFragment1
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        viewF = inflater.inflate(R.layout.fragment_reports,container,false)
        auth = FirebaseAuth.getInstance()
        pref = this.requireActivity().getSharedPreferences("Preferences2", Context.MODE_PRIVATE)
        reportViewModel = ViewModelProvider(this).get(ReportViewModel::class.java)
        changeList()
        initRecycler(emptyList())
        return viewF
    }

    fun changeList(){
        if(pref.getString(AppConstants.SAVE_FRAGMENT, "") == "0"){
            reportViewModel.getReportPerUser(auth.currentUser!!.email).observe(viewLifecycleOwner, { match ->
                viewAdapter.dataChange(match)
            })
        }
        else{
            reportViewModel.allReports.observe(viewLifecycleOwner, { match ->
                viewAdapter.dataChange(match)
            })
        }
    }

    fun initRecycler(match : List<Report>){
        viewManager = LinearLayoutManager(context)
        viewAdapter = ReportAdapter(match,{ matchItem: Report-> onClicked(matchItem)}, getString(R.string.pending), getString(R.string.done))
        viewF.recycler.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    fun onClicked(item : Report){
        val extras = Bundle()
        extras.putParcelable(REPORT_KEY, item)
        startActivity(Intent(context, ReportInfoActivity::class.java).putExtras(extras))
    }
}