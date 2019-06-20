package com.mapache.safeuca.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mapache.safeuca.R
import com.mapache.safeuca.database.entities.Report
import com.mapache.safeuca.database.entities.Zone
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import kotlinx.android.synthetic.main.initial_dialog.view.*
import kotlinx.android.synthetic.main.zone_dialog.view.*

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mBottomSheetDialog : BottomSheetDialog
    private lateinit var reportViewModel : ReportViewModel
    private lateinit var marker : Marker
    private lateinit var zone : Zone
    var click : newReportClick? = null

    companion object{
        fun newInstance (mBottomSheetDialog: BottomSheetDialog): MapsFragment {
            val newFragment = MapsFragment()
            newFragment.mBottomSheetDialog = mBottomSheetDialog
            return  newFragment
        }
    }

    interface newReportClick{
        fun newReportClick(latLng: LatLng)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is newReportClick) click = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_maps,container,false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        reportViewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)

        val contentDialog = layoutInflater.inflate(R.layout.initial_dialog, null)
        val contentZoneDialog = layoutInflater.inflate(R.layout.zone_dialog, null)
        val contentBuildingDialog = layoutInflater.inflate(R.layout.building_dialog, null)

        mBottomSheetDialog.setContentView(contentDialog)
        setOnClickListeners(contentDialog, contentZoneDialog, contentBuildingDialog)
        return view
    }

    fun setOnClickListeners(contentInitialDialog : View, contentZoneDialog: View, contentBuildingDialog : View){
        mBottomSheetDialog.setOnDismissListener{
            marker.remove()
        }

        contentInitialDialog.initial_no.setOnClickListener {
            mBottomSheetDialog.dismiss()
        }

        contentInitialDialog.initial_si.setOnClickListener {
            if(inZone(marker.position)){
                mBottomSheetDialog.setContentView(contentZoneDialog)
            } else{
                click?.newReportClick(marker.position)
                mBottomSheetDialog.dismiss()
            }
        }

        contentZoneDialog.zone_no.setOnClickListener {
            click?.newReportClick(marker.position)
        }

        contentZoneDialog.zone_si.setOnClickListener {
            if(zone.building != null){
                mBottomSheetDialog.setContentView(contentBuildingDialog)
            } else{
                click?.newReportClick(marker.position)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        click = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        initMap(mMap)

        val uca = LatLng(13.6816, -89.235)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uca, 18F))

        mMap.setOnMapClickListener {
            marker = mMap.addMarker(MarkerOptions().position(it).title("Zona de riesgo"))
            Log.d("Hola", it.latitude.toString() + " " + it.longitude.toString())
            mBottomSheetDialog.show()
        }
    }

    fun initMap(mMap : GoogleMap){
        reportViewModel.allReports.observe(this, Observer {
            for(report : Report in it){
                mMap.addMarker(MarkerOptions().position(LatLng(report.lat, report.ltn)).title(report.name))
            }
        })
    }

    fun inZone(latLng: LatLng) : Boolean{
        return false
    }
}
