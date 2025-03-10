package com.mapache.safeuca.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.mapache.safeuca.R
import com.mapache.safeuca.activities.ReportInfoActivity
import com.mapache.safeuca.database.entities.Report
import com.mapache.safeuca.database.entities.Zone
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import com.mapache.safeuca.utilities.AppConstants
import com.mapache.safeuca.utilities.AppConstants.ADMIN_FLAG
import com.mapache.safeuca.utilities.AppConstants.REPORT_KEY
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
    lateinit var mapTheme : SharedPreferences

    companion object{
        fun newInstance (): MapsFragment {
            val newFragment = MapsFragment()
            return  newFragment
        }
    }

    interface newReportClick{
        fun newReportClick(latLng: LatLng, idZone : String, level: Int)
        fun checkNetworkStatus() : Boolean
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
        initData()
        setOnClickListeners()
        return view
    }

    fun initData(){
        auth = FirebaseAuth.getInstance()

        reportViewModel = ViewModelProvider(this).get(ReportViewModel::class.java)
        mBottomSheetDialog = context?.let { BottomSheetDialog(it) }!!

        mapTheme = this.requireActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE)

        contentInitialDialog = layoutInflater.inflate(R.layout.initial_dialog, null)
        contentZoneDialog = layoutInflater.inflate(R.layout.zone_dialog, null)
        contentBuildingDialog = layoutInflater.inflate(R.layout.building_dialog, null)

    }

    fun setOnClickListeners(){
        mBottomSheetDialog.setOnDismissListener{
            marker.remove()
            if(flagZone){
                flagZone = false
            }
            if(flagBuilding){
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
                click?.newReportClick(marker.position, "5d13aa0b5003f10017fb2cc0", -1)
                mBottomSheetDialog.dismiss()
            }
        }

        contentZoneDialog.zone_no.setOnClickListener {
            click?.newReportClick(marker.position, "5d13aa0b5003f10017fb2cc0", -1)
            mBottomSheetDialog.dismiss()
        }

        contentZoneDialog.zone_si.setOnClickListener {
            if(zone.building == 1){
                if(zone.level > 1){
                    mBottomSheetDialog.setContentView(contentBuildingDialog)
                    mBottomSheetDialog.spinner_building.adapter = ArrayAdapter(context, R.layout.simple_spinner_item, R.id.item_spinner, (1..zone.level).toList().toTypedArray())
                    flagBuilding = true
                }
                else if(zone.level < -1){
                    mBottomSheetDialog.setContentView(contentBuildingDialog)
                    var arrayList = ArrayList<Int>()
                    var i = 0
                    while(i < zone.level*-1){
                        arrayList.add(i)
                        i++
                    }
                    flagBuilding = true
                    mBottomSheetDialog.spinner_building.adapter = ArrayAdapter(context, R.layout.simple_spinner_item, R.id.item_spinner, arrayList)
                }
                else if(zone.level == 1){
                    click?.newReportClick(marker.position, zone.id, -1)
                    mBottomSheetDialog.dismiss()
                }
            } else{
                click?.newReportClick(marker.position, zone.id, -1)
                mBottomSheetDialog.dismiss()
            }
        }

        contentBuildingDialog.building_ok.setOnClickListener {
            click?.newReportClick(marker.position, zone.id, buildingSelected)
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

    fun setMapTheme(){
        if (mapTheme.getString(AppConstants.SAVE_THEME, "") == "0"){
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_standar))
        }
        else{
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_night))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapTheme()
        initMap(mMap)
        initZones(mMap)

        val uca = LatLng(13.6816, -89.235)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uca, 18F))

        if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) } == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(mMap.myLocation.latitude, mMap.myLocation.longitude), 18F))
        }

        mMap.setOnMapClickListener { latLng ->
            if (auth.currentUser != null){
                if(click?.checkNetworkStatus()!!){
                    if(inUca(latLng)){
                        marker = mMap.addMarker(MarkerOptions().position(latLng))
                        mBottomSheetDialog.setContentView(contentInitialDialog)
                        mBottomSheetDialog.show()
                    } else{
                        Toast.makeText(context, getString(R.string.reports_inside_uca), Toast.LENGTH_LONG).show()
                    }

                } else{
                    Toast.makeText(context, getString(R.string.internet_required), Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(context, getString(R.string.logIn_to_report), Toast.LENGTH_LONG).show()
            }
        }

        mMap.setOnMarkerClickListener {
            val extras = Bundle()
            extras.putParcelable(REPORT_KEY, it.tag as Report)
            startActivity(Intent(context, ReportInfoActivity::class.java).putExtras(extras))
            true
        }
    }

    fun initMap(mMap : GoogleMap){
        reportViewModel.allReports.observe(this, Observer {
            it.forEach{
                if(it.status != "2"){
                    if(it.type == "0" && it.status == "1"){
                        val marker = mMap.addMarker(MarkerOptions().position(LatLng(it.lat, it.ltn)).title(it.name))
                        marker.tag = it
                        marker.setIcon(context?.let { it1 -> getBitmapFromVectorDrawable(it1, R.drawable.ic_report_48dp) })
                    }
                    else if(it.type == "1" && it.status == "1"){
                        val marker = mMap.addMarker(MarkerOptions().position(LatLng(it.lat, it.ltn)).title(it.name))
                        marker.tag = it
                        marker.setIcon(this!!.context?.let { it1 -> getBitmapFromVectorDrawable(it1, R.drawable.ic_maintenance_48dp) })
                    }
                    else if(it.type == "2" && it.status == "1"){
                        val marker = mMap.addMarker(MarkerOptions().position(LatLng(it.lat, it.ltn)).title(it.name))
                        marker.tag = it
                        marker.setIcon(this!!.context?.let { it1 -> getBitmapFromVectorDrawable(it1, R.drawable.ic_desinfection_48dp) })
                    }
                    if(it.status == "0" && ADMIN_FLAG){
                        val marker = mMap.addMarker(MarkerOptions().position(LatLng(it.lat, it.ltn)).title(it.name))
                        marker.tag = it
                        marker.setIcon(this!!.context?.let { it1 -> getBitmapFromVectorDrawable(it1, R.drawable.ic_pending_48dp) })
                    }
                }
            }
        })
    }

    private fun getBitmapFromVectorDrawable(context : Context , drawableId : Int ) : BitmapDescriptor {
        var drawable : Drawable? = ContextCompat.getDrawable(context, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable!!)).mutate()
        }
        val bitmap : Bitmap = Bitmap.createBitmap(drawable!!.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
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

    fun inUca(cor : LatLng) : Boolean{
        for(pp in arrayPolygon){
            if((pp.tag as Zone).id == "5d13aa0b5003f10017fb2cc0" ){
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
                return angulo == 6.2831
            }
        }
        return false
    }

    fun inZone(cor : LatLng) : Boolean{
        for(pp in arrayPolygon){
            var angulo = 0.0
            var i = 0
            if(pp.points.size == 5){
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
                if(angulo >= 6.2831){
                    polygon = pp
                    return true
                }
            }
        }
        return false
    }
}


