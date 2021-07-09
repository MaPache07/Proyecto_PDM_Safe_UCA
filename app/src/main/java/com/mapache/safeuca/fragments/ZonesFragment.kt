package com.mapache.safeuca.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mapache.safeuca.R
import com.mapache.safeuca.adapter.ZoneAdapter
import com.mapache.safeuca.database.entities.Zone
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import com.mapache.safeuca.utilities.AppConstants.SAVE_FRAGMENT
import kotlinx.android.synthetic.main.fragment_zones.view.*

class ZonesFragment : Fragment(){

    private lateinit var auth: FirebaseAuth
    private lateinit var zoneViewModel : ReportViewModel
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: ZoneAdapter
    private lateinit var reportsFragment: ReportsFragment
    lateinit var infoZoneFragment: InfoZoneFragment
    lateinit var saveFragment : SharedPreferences
    var click : ChangeFragmentZone? = null

    companion object{
        fun newInstance (): ZonesFragment {
            val newFragmentZone = ZonesFragment()
            return  newFragmentZone
        }
    }

    interface ChangeFragmentZone{
        fun changeFragmentZone(infoZoneFragment: InfoZoneFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is ChangeFragmentZone) click = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_zones,container,false)
        auth = FirebaseAuth.getInstance()
        saveFragment = this.requireActivity().getSharedPreferences("Preferences2", Context.MODE_PRIVATE)
        reportsFragment = ReportsFragment.newInstance()
        zoneViewModel = ViewModelProvider(this).get(ReportViewModel::class.java)
        zoneViewModel.allZones.observe(viewLifecycleOwner, { match ->
            viewAdapter.dataChange(match)
        })
        initRecycler(emptyList(), view)
        return view
    }

    fun initRecycler(match : List<Zone>, view: View){
        viewManager = LinearLayoutManager(context)
        viewAdapter = ZoneAdapter(match,{ zoneItem: Zone-> onClickedZone(zoneItem)})
        view.recycler_zones.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    fun onClickedZone(item : Zone){
        saveFragment.edit().putString(SAVE_FRAGMENT, "3").apply()
        infoZoneFragment = InfoZoneFragment.newInstance(item)
        click?.changeFragmentZone(infoZoneFragment)
    }

    override fun onDetach() {
        super.onDetach()
        click = null
    }
}