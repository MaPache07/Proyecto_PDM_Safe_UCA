package com.mapache.safeuca.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.mapache.safeuca.R
import com.mapache.safeuca.database.entities.Zone
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import com.mapache.safeuca.utilities.AppConstants
import kotlinx.android.synthetic.main.building_dialog.*
import kotlinx.android.synthetic.main.building_dialog.view.*
import kotlinx.android.synthetic.main.initial_dialog.view.*
import kotlinx.android.synthetic.main.zone_dialog.*
import kotlinx.android.synthetic.main.zone_dialog.view.*

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mBottomSheetDialog : BottomSheetDialog
    private lateinit var reportViewModel : ReportViewModel
    private lateinit var marker : Marker
    private lateinit var flag : TextView
    private lateinit var auth: FirebaseAuth
    var arrayPolygon = ArrayList<Polygon>()
    lateinit var polygon : Polygon
    lateinit var zone : Zone
    lateinit var contentInitialDialog : View
    lateinit var contentZoneDialog : View
    lateinit var contentBuildingDialog : View
    var buildingSelected : Int = 0
    var flagZone = false
    var flagBuilding = false
    var click : newReportClick? = null

    companion object{
        fun newInstance (): MapsFragment {
            val newFragment = MapsFragment()
            return  newFragment
        }
    }

    interface changeTheme{
        fun changeTheme()
    }
    interface newReportClick{
        fun newReportClick(latLng: LatLng, zone : Zone, level: Int)
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
        auth = FirebaseAuth.getInstance()
        Log.d("Hola", "OnCreateView MapsFragment")

        reportViewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)
        mBottomSheetDialog = context?.let { BottomSheetDialog(it) }!!
        reportViewModel.getZonesAzync()
        reportViewModel.getReportsAsync()

        flag = activity!!.findViewById(R.id.tv_escondido)

        contentInitialDialog = layoutInflater.inflate(R.layout.initial_dialog, null)
        contentZoneDialog = layoutInflater.inflate(R.layout.zone_dialog, null)
        contentBuildingDialog = layoutInflater.inflate(R.layout.building_dialog, null)

        setOnClickListeners()
        return view
    }

    fun setOnClickListeners(){
        mBottomSheetDialog.setOnDismissListener{
            marker.remove()
            (contentInitialDialog.parent as ViewGroup).removeAllViews()
            if(flagZone){
                (contentZoneDialog.parent as ViewGroup).removeAllViews()
                flagZone = false
            }
            if(flagBuilding){
                (contentBuildingDialog.parent as ViewGroup).removeAllViews()
                flagBuilding = false
            }
        }

        contentInitialDialog.initial_no.setOnClickListener {
            mBottomSheetDialog.dismiss()
        }

        contentInitialDialog.initial_si.setOnClickListener {
            if(inZone(marker.position)){
                zone = polygon.tag as Zone
                val text = getString(R.string.zoneDialog) + " " + zone.name + "?"
                mBottomSheetDialog.setContentView(contentZoneDialog)
                flagZone = true
                mBottomSheetDialog.zone_dialog_tv.text = text
            } else{
                click?.newReportClick(marker.position, zone, -1)
                mBottomSheetDialog.dismiss()
            }
        }

        contentZoneDialog.zone_no.setOnClickListener {
            click?.newReportClick(marker.position, zone, -1)
        }

        contentZoneDialog.zone_si.setOnClickListener {
            if(zone.building == 1 && zone.level > 1){
                mBottomSheetDialog.setContentView(contentBuildingDialog)
                mBottomSheetDialog.spinner_building.adapter = ArrayAdapter(context, R.layout.simple_spinner_item, R.id.item_spinner, (1..zone.level).toList().toTypedArray())
                flagBuilding = true
            } else{
                click?.newReportClick(marker.position, zone, -1)
                mBottomSheetDialog.dismiss()
            }
        }

        contentBuildingDialog.building_ok.setOnClickListener {
            click?.newReportClick(marker.position, zone, buildingSelected)
            mBottomSheetDialog.dismiss()
        }

        contentBuildingDialog.spinner_building.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                buildingSelected = parent?.getItemAtPosition(0) as Int
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                buildingSelected = parent?.getItemAtPosition(position) as Int
            }

        }
    }

    override fun onDetach() {
        super.onDetach()
        click = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (flag.text== "0"){
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_standar))
        }
        else{
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_night))
        }
        initMap(mMap)
        initZones(mMap)
        val uca = LatLng(13.6816, -89.235)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uca, 18F))

        mMap.setOnMapClickListener { latLng ->
            if (auth.currentUser != null){
                marker = mMap.addMarker(MarkerOptions().position(latLng).title("Zona de riesgo"))
                mBottomSheetDialog.setContentView(contentInitialDialog)
                mBottomSheetDialog.show()
            }
            else{
                Toast.makeText(context, "Inicia sesion para reportar", Toast.LENGTH_LONG).show()
            }
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
                arrayPolygon.add(polygon)
            }
        })
    }

    fun inZone(cor : LatLng) : Boolean{
        for(pp in arrayPolygon){
            var angulo = 0.0
            var i = 0
            while(i < pp.points.size-1){
                var j = 1
                if(i == pp.points.size-2){
                    j = -i
                }
                var x1 = cor.latitude - pp.points[i].latitude
                var y1 = cor.longitude - pp.points[i].longitude
                var x2 = cor.latitude - pp.points[i+j].latitude
                var y2 = cor.longitude - pp.points[i+j].longitude
                var xy1 = x1*x2 + y1*y2
                var xy2 = Math.sqrt(Math.pow(x1, 2.0)+Math.pow(y1, 2.0))*Math.sqrt(Math.pow(x2, 2.0)+Math.pow(y2, 2.0))
                angulo += Math.acos(xy1/xy2)
                i++
            }
            angulo = Math.floor(angulo*1e4)/1e4
            if(angulo == 6.2831){
                polygon = pp
                return true
            }
        }
        return false
    }
}


