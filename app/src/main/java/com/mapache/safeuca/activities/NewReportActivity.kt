package com.mapache.safeuca.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.mapache.safeuca.R
import com.mapache.safeuca.database.entities.Report
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import com.mapache.safeuca.utilities.AppConstants
import com.mapache.safeuca.utilities.AppConstants.CAMERA_REQUEST_CODE
import com.mapache.safeuca.utilities.AppConstants.GALERY_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_new_report.*
import kotlinx.android.synthetic.main.image_dialog.*
import kotlinx.android.synthetic.main.image_dialog.view.*
import kotlinx.android.synthetic.main.initial_dialog.view.*
import java.io.ByteArrayOutputStream

class NewReportActivity : AppCompatActivity() {

    private lateinit var reportViewModel : ReportViewModel
    lateinit var arrayDanger : Array<String>
    lateinit var arrayType : Array<String>
    private lateinit var auth : FirebaseAuth
    lateinit var dangerSelected : String
    lateinit var typeSelected : String
    lateinit var latLng : LatLng
    lateinit var idZone : String
    var level : Int = -1
    lateinit var image : String
    lateinit var bitmap : Bitmap
    lateinit var imageIntent : Intent
    lateinit var contentImageDialog : View
    private lateinit var mBottomSheetDialog : BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_report)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        FirebaseApp.initializeApp(this@NewReportActivity)

        initData()
    }

    fun initData(){
        reportViewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)
        auth = FirebaseAuth.getInstance()

        arrayDanger = arrayOf(getString(R.string.low), getString(R.string.moderate), getString(R.string.high))
        arrayType = arrayOf(getString(R.string.report), getString(R.string.maintenance), getString(R.string.desinfection))

        latLng = intent?.extras?.getParcelable(AppConstants.LATLNT_KEY)!!
        idZone = intent?.extras?.getString(AppConstants.ZONE_KEY)!!
        level = intent?.extras?.getInt(AppConstants.LEVEL_KEY)!!

        mBottomSheetDialog = BottomSheetDialog(this)
        contentImageDialog = layoutInflater.inflate(R.layout.image_dialog, null)
        image = ""

        initSpinners()
        setOnClickListeners()
    }

    fun setOnClickListeners(){
        report_image_action.setOnClickListener {
            mBottomSheetDialog.setContentView(contentImageDialog)
            mBottomSheetDialog.show()
        }
        contentImageDialog.image_galery.setOnClickListener {
            imageIntent = Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imageIntent.setType("image/")
            mBottomSheetDialog.dismiss()
            startActivityForResult(Intent.createChooser(imageIntent, getString(R.string.choose_app)), GALERY_REQUEST_CODE)
        }
        contentImageDialog.image_camera.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            }
            else {
                imageIntent = Intent (MediaStore.ACTION_IMAGE_CAPTURE)
                mBottomSheetDialog.dismiss()
                startActivityForResult(imageIntent, CAMERA_REQUEST_CODE)
            }
        }

        new_report_ok.setOnClickListener {
            if(TextUtils.isEmpty(new_report_name.text) || TextUtils.isEmpty(new_report_description.text) || image == ""){
                Toast.makeText(applicationContext, getString(R.string.all_data), Toast.LENGTH_LONG).show()
            } else{
                val retroRepo = Report("", new_report_name.text.trim().toString(), dangerSelected, typeSelected,
                    "0", auth.currentUser?.email!!, image, new_report_description.text.trim().toString(),
                    latLng.latitude, latLng.longitude, idZone, level)
                if(checkNetworkStatus()) reportViewModel.postReport(retroRepo)
                else Toast.makeText(applicationContext, getString(R.string.internet_required), Toast.LENGTH_LONG).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
        new_report_cancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    fun initSpinners(){
        spinner_danger.adapter = ArrayAdapter<String>(this, R.layout.simple_spinner_item, R.id.item_spinner, arrayDanger)
        spinner_type.adapter = ArrayAdapter<String>(this, R.layout.simple_spinner_item, R.id.item_spinner, arrayType)

        spinner_danger.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                dangerSelected = "0"
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                dangerSelected = position.toString()
            }
        }

        spinner_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                typeSelected = "0"
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                typeSelected = position.toString()
            }

        }
    }

    fun checkNetworkStatus() : Boolean{
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        return if(connectivityManager is ConnectivityManager){
            val networkInfo : NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK) {
            if(requestCode == GALERY_REQUEST_CODE){
                val path : Uri = data!!.data
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, path)
            }
            else if(requestCode == CAMERA_REQUEST_CODE){
                bitmap = data!!.extras.get("data") as Bitmap
            }
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 170, 170, true)
            val baos = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            report_image.setImageBitmap(resizedBitmap)
            image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
        }
    }
}
