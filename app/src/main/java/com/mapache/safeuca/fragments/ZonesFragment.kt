package com.mapache.safeuca.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mapache.safeuca.R
import com.mapache.safeuca.activities.ZoneInfoActivity
import com.mapache.safeuca.adapter.ZoneAdapter
import com.mapache.safeuca.database.entities.Zone
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import kotlinx.android.synthetic.main.fragment_zones.view.*

class ZonesFragment : Fragment(){
    private lateinit var auth: FirebaseAuth
    private lateinit var zoneViewModel : ReportViewModel
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: ZoneAdapter
    companion object{
        fun newInstance (): ZonesFragment {
            val newFragmentZone = ZonesFragment()
            return  newFragmentZone
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_zones,container,false)
        auth = FirebaseAuth.getInstance()
        zoneViewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)

        zoneViewModel.allZones.observe(this, Observer { match ->
            viewAdapter.dataChange(match)
        })
        initRecycler(emptyList(), view)
        return view
    }

    fun initRecycler(match : List<Zone>, view: View){
        viewManager = LinearLayoutManager(context)
        viewAdapter = ZoneAdapter(match,{ zoneItem: Zone-> onClicked(zoneItem)})
        view.recycler_zones.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    fun onClicked(item : Zone){
        val extras = Bundle()
        extras.putString("name",item.name)
        extras.putString("idZone", item.id)
        startActivity(Intent(context, ZoneInfoActivity::class.java).putExtras(extras))
    }
}