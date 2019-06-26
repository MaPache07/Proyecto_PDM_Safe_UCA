package com.mapache.safeuca.activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.model.LatLng
import com.mapache.safeuca.R
import com.mapache.safeuca.database.entities.Report
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import com.mapache.safeuca.utilities.AppConstants
import com.mapache.safeuca.utilities.AppConstants.MEDIA_DIRECTORY
import com.mapache.safeuca.utilities.AppConstants.PHOTO_CODE
import com.mapache.safeuca.utilities.AppConstants.SELECT_FILE
import com.mapache.safeuca.utilities.AppConstants.TEMPORAL_PICTURE_NAME
import kotlinx.android.synthetic.main.activity_new_report.*
import java.io.File

class NewReportActivity : AppCompatActivity() {

    private lateinit var reportViewModel : ReportViewModel
    val arrayDanger : Array<String> = arrayOf("Bajo", "Medio", "Alto")
    val arrayType : Array<String> = arrayOf("Reporte", "Mantenimiento")
    lateinit var dangerSelected : String
    lateinit var typeSelected : String
    lateinit var latLng : LatLng
    lateinit var idZone : String
    lateinit var fileName : String
    var level : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_report)
        reportViewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)

        latLng = intent?.extras?.getParcelable(AppConstants.LATLNT_KEY)!!
        idZone = intent?.extras?.getString(AppConstants.IDZONE_KEY)!!
        level = intent?.extras?.getInt(AppConstants.LEVEL_KEY)!!

        initSpinners()
        setOnClickListeners()
    }

    fun setOnClickListeners(){
        action_foto.setOnClickListener {
            openOptions()
        }

        new_report_ok.setOnClickListener {
            if(TextUtils.isEmpty(new_report_name.text) && TextUtils.isEmpty(new_report_description.text)){
                Toast.makeText(applicationContext, "Ingrese todos los datos", Toast.LENGTH_LONG).show()
            } else{
                //val report = Report("", new_report_name.text.trim().toString(), 1, typeSelected, )
            }
        }
    }

    private fun openOptions(){
        val options = arrayOf("Tomar foto", "Elegir de Galeria", "Cancelar")
        var builder : AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Elige una Opción")
        builder.setItems(options) { dialogInterface: DialogInterface, i: Int -> run {
            if (options[i] == "Tomar foto") {
                openCamara()
            } else if(options[i] == "Elegir de Galeria"){
                openGallery()
            } else if(options[i] == "Cancelar"){
                dialogInterface.dismiss()
            }
        }
        }.show()
    }

    private fun openCamara(){
        var tempFile = File.createTempFile("image", ".jpg")
        fileName = tempFile.absolutePath
        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile))
        startActivityForResult(intent, PHOTO_CODE)
    }

    private fun openGallery(){
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Seleccione la imagen!!"), SELECT_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var selectedImage : Uri?
        when(requestCode){
            SELECT_FILE -> {
                if (resultCode == Activity.RESULT_OK){
                    selectedImage = data!!.data
                    new_image_report.setImageURI(selectedImage)
                }
            }
            PHOTO_CODE -> {
                if (resultCode == Activity.RESULT_OK){
                    Log.d("Hola", "Si entra we!!")
                    var path : String = Environment.getExternalStorageDirectory().toString() + File.separator + MEDIA_DIRECTORY + File.separator + TEMPORAL_PICTURE_NAME
                    new_image_report.setImageURI(path.toUri())
                }
            }
        }
    }

    fun initSpinners(){
        spinner_danger.adapter = ArrayAdapter<String>(this, R.layout.simple_spinner_item, R.id.item_spinner, arrayDanger)
        spinner_type.adapter = ArrayAdapter<String>(this, R.layout.simple_spinner_item, R.id.item_spinner, arrayType)

        spinner_danger.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                dangerSelected = parent?.getItemAtPosition(0) as String
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                dangerSelected = parent?.getItemAtPosition(position) as String
            }
        }

        spinner_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                typeSelected = parent?.getItemAtPosition(0) as String
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                typeSelected = parent?.getItemAtPosition(position) as String
            }

        }
    }
}
