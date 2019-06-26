package com.mapache.safeuca.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mapache.safeuca.R
import kotlinx.android.synthetic.main.activity_report_info.*

class ReportInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_info)

        val intento = intent
        if(intento != null){
            var name = intento.getStringExtra("name")
            var danger = intento.getStringExtra("danger")
            var type = intento.getStringExtra("type")
            var status = intento.getStringExtra("status")
            var mailUser = intento.getStringExtra("mail")
            var description = intento.getStringExtra("desc")
            var latitud = intento.getDoubleExtra("lat", 0.0)
            var longitud = intento.getDoubleExtra("long", 0.0)
            var level = intento.getIntExtra("level", 0)
            var img = intento.getStringExtra("img")
            ar_name.text = name
            ar_danger.text = danger
            ar_type.text = type
            ar_status.text = status
            ar_user.text = mailUser
            ar_description.text = description
            ar_latitud.text = latitud.toString()
            ar_longitud.text = longitud.toString()
            ar_level.text = level.toString()
        }
    }
}
