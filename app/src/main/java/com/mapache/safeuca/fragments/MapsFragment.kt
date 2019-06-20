package com.mapache.safeuca.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mapache.safeuca.R
import kotlinx.android.synthetic.main.content_dialog.view.*

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mBottomSheetDialog : BottomSheetDialog
    private var marker : MarkerOptions? = null

    companion object{
        fun newInstance (mBottomSheetDialog: BottomSheetDialog): MapsFragment {
            val newFragment = MapsFragment()
            newFragment.mBottomSheetDialog = mBottomSheetDialog
            return  newFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_maps,container,false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val contentDialog = layoutInflater.inflate(R.layout.content_dialog, null)
        mBottomSheetDialog.setContentView(contentDialog)

        contentDialog.action_no.setOnClickListener {
            mBottomSheetDialog.dismiss()
        }

        contentDialog.action_si.setOnClickListener {

        }

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val uca = LatLng(13.6816, -89.235)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uca, 18F))

        mMap.setOnMapClickListener {
            marker = MarkerOptions().position(it).title("Zona de riesgo")
            mMap.addMarker(marker)
            mBottomSheetDialog.show()
        }
    }
}
