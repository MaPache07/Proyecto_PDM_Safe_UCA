package com.mapache.safeuca.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mapache.safeuca.R
import com.mapache.safeuca.database.entities.Zone
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import kotlinx.android.synthetic.main.initial_dialog.view.*
import kotlinx.android.synthetic.main.zone_dialog.view.*

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mBottomSheetDialog : BottomSheetDialog
    private lateinit var reportViewModel : ReportViewModel
    private lateinit var marker : Marker
    private var zone : Zone? = null
    var click : newReportClick? = null

    companion object{
        fun newInstance (mBottomSheetDialog: BottomSheetDialog): MapsFragment {
            val newFragment = MapsFragment()
            newFragment.mBottomSheetDialog = mBottomSheetDialog
            return  newFragment
        }
    }

    interface newReportClick{
        fun newReportClick(latLng: LatLng, level: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is newReportClick) click = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_maps,container,false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        reportViewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)
        reportViewModel.getZonesAzync()
        reportViewModel.getReportsAsync()


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
            if(zone != null){
                mBottomSheetDialog.setContentView(contentZoneDialog)
            } else{
                click?.newReportClick(marker.position, -1)
                mBottomSheetDialog.dismiss()
            }
        }

        contentZoneDialog.zone_no.setOnClickListener {
            click?.newReportClick(marker.position, -1)
        }

        contentZoneDialog.zone_si.setOnClickListener {
            if(zone?.building == 1 && zone?.level!! > 1){
                mBottomSheetDialog.setContentView(contentBuildingDialog)
            } else{
                click?.newReportClick(marker.position, -1)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        click = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_standar))
        initMap(mMap)
        initZones(mMap)
        val uca = LatLng(13.6816, -89.235)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uca, 18F))
        
        /*mMap.setOnPolygonClickListener {
            zone = it.tag as Zone
        }*/

        mMap.setOnMapClickListener { latLng ->
            marker = mMap.addMarker(MarkerOptions().position(latLng).title("Zona de riesgo"))
            mBottomSheetDialog.show()
        }
    }

    fun initMap(mMap : GoogleMap){
        reportViewModel.allReports.observe(this, Observer {
            it.forEach{
                mMap.addMarker(MarkerOptions().position(LatLng(it.lat, it.ltn)).title(it.name))
            }
        })
    }

    fun initZones(mMap: GoogleMap){
        reportViewModel.allZones.observe(this, Observer {
            it.forEach{
                var i = 0
                var latArray = it.arrayLat.substring(1, it.arrayLat.length-1).split(',')
                var lngArray = it.arrayLng.substring(1, it.arrayLng.length-1).split(',')
                var polygonOptions = PolygonOptions()
                while(i < latArray.size){
                    polygonOptions.add(LatLng(latArray[i].toDouble(), lngArray[i].toDouble()))
                    i++
                }
                val polygon = mMap.addPolygon(polygonOptions)
                polygon.fillColor = Color.TRANSPARENT
                polygon.strokeWidth = 0F
                polygon.tag = it
                polygon.isClickable = true

            }
        })
    }
}


