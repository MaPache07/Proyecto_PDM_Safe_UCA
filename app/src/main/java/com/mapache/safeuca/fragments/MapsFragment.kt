package com.mapache.safeuca.fragments

import android.content.Context
import android.os.Bundle
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
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import kotlinx.android.synthetic.main.content_dialog.view.*

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mBottomSheetDialog : BottomSheetDialog
    private lateinit var reportViewModel : ReportViewModel
    private lateinit var marker : Marker
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

        val contentDialog = layoutInflater.inflate(R.layout.content_dialog, null)
        mBottomSheetDialog.setContentView(contentDialog)

        contentDialog.action_no.setOnClickListener {
            marker.remove()
            mBottomSheetDialog.dismiss()
        }

        contentDialog.action_si.setOnClickListener {
            if(inZone(marker.position)){

            } else{
                click?.newReportClick(marker.position)
                marker.remove()
                mBottomSheetDialog.dismiss()
            }
        }

        return view
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
