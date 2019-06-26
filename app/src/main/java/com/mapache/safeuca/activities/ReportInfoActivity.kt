package com.mapache.safeuca.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
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
            var level = intento.getIntExtra("level", 0)
            ar_name.text = name
            ar_danger.text = danger
            ar_type.text = type
            ar_status.text = status
            ar_user.text = mailUser
            ar_description.text = description
            if(level == -1) level_info.isVisible = false
            else ar_level.text = level.toString()
        }
    }
}
