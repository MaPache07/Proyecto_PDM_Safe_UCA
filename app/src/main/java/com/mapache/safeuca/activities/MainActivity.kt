package com.mapache.safeuca.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.mapache.safeuca.R
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import com.mapache.safeuca.fragments.InfoZoneFragment
import com.mapache.safeuca.fragments.MapsFragment
import com.mapache.safeuca.fragments.ReportsFragment
import com.mapache.safeuca.fragments.ZonesFragment
import com.mapache.safeuca.utilities.AppConstants
import com.mapache.safeuca.utilities.AppConstants.CAMERA_REQUEST_CODE
import com.mapache.safeuca.utilities.AppConstants.LOCATION_REQUEST_CODE
import com.mapache.safeuca.utilities.AppConstants.MY_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MapsFragment.newReportClick, ZonesFragment.ChangeFragmentZone {

    private lateinit var fragmentMap : MapsFragment
    private lateinit var auth: FirebaseAuth
    private lateinit var reportsFragment: ReportsFragment
    private lateinit var zonesFragment : ZonesFragment
    lateinit var providers : List<AuthUI.IdpConfig>
    private lateinit var reportViewModel : ReportViewModel
    lateinit var saveTheme : SharedPreferences
    lateinit var saveFragment : SharedPreferences

    lateinit var logIn : MenuItem
    lateinit var map : MenuItem
    lateinit var myReports : MenuItem
    lateinit var allReports : MenuItem
    lateinit var dark : MenuItem
    lateinit var bright : MenuItem
    lateinit var logout : MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        FirebaseApp.initializeApp(this@MainActivity)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        var flag = true
        if(savedInstanceState != null){
            if(saveFragment.getString(AppConstants.SAVE_FRAGMENT, "") == "")
                changeFragment(R.id.fragment_map, fragmentMap)
            else {
                when(saveFragment.getString(AppConstants.SAVE_FRAGMENT, "")){
                    "0" -> changeFragment(R.id.fragment_map, reportsFragment)
                    "1" -> changeFragment(R.id.fragment_map, reportsFragment)
                    "2" -> changeFragment(R.id.fragment_map, zonesFragment)
                }
                dark.isVisible = false
                bright.isVisible = false
            }
            flag = false
        }
        else{
            saveFragment.edit().putString(AppConstants.SAVE_FRAGMENT, "").apply()
            if(checkNetworkStatus()){
                reportViewModel.getReportsAsync()
                reportViewModel.getZonesAzync()
            } else{
                Toast.makeText(this, getString(R.string.internet_required_map), Toast.LENGTH_LONG).show()
            }
            if(saveTheme.getString(AppConstants.SAVE_THEME, "") == "")
                saveTheme.edit().putString(AppConstants.SAVE_THEME, "0").apply()
            changeFragment(R.id.fragment_map, fragmentMap)
        }

        navView.setNavigationItemSelectedListener(this)
        if(auth.currentUser != null){
            val user = FirebaseAuth.getInstance().currentUser
            if(flag) Toast.makeText(this, user!!.email, Toast.LENGTH_SHORT).show()
            logIn.isVisible = false
            logout.isVisible = true
            myReports.isVisible = true
            allReports.isVisible = true
            dark.isVisible = true
            bright.isVisible = false
        }
    }

    override fun onStart() {
        super.onStart()
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            }
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            }
        }
    }

    fun initData(){
        reportViewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)

        saveTheme = getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        saveFragment = getSharedPreferences("Preferences2", Context.MODE_PRIVATE)
        fragmentMap = MapsFragment.newInstance()
        reportsFragment = ReportsFragment.newInstance()
        zonesFragment = ZonesFragment.newInstance()
        auth = FirebaseAuth.getInstance()

        logIn = nav_view.menu[0]
        map = nav_view.menu[1]
        myReports = nav_view.menu[2]
        allReports = nav_view.menu[3]
        dark = nav_view.menu[4]
        bright = nav_view.menu[5]
        logout = nav_view.menu[6]

        providers = Arrays.asList<AuthUI.IdpConfig>(
            AuthUI.IdpConfig.EmailBuilder().build(),//email login
            AuthUI.IdpConfig.GoogleBuilder().build()) //google login
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("1", 1)
    }

    override fun checkNetworkStatus() : Boolean{
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        return if(connectivityManager is ConnectivityManager){
            val networkInfo : NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    override fun changeFragmentZone(infoZoneFragment: InfoZoneFragment) {
        changeFragment(R.id.fragment_map, infoZoneFragment)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(this, getString(R.string.logIn_successful), Toast.LENGTH_LONG).show()
                correo_en_nav.text = user!!.email
                nombre_en_nav.text = user.displayName
                logIn.isVisible = false
                logout.isVisible = true
                myReports.isVisible = true
                allReports.isVisible = true
                Glide.with(this).load(auth.currentUser!!.photoUrl).apply(RequestOptions.circleCropTransform()).into(imageView)
            } else Toast.makeText(this, getString(R.string.logIn_error), Toast.LENGTH_LONG).show()
        }
        if(requestCode == AppConstants.REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK)
                if(checkNetworkStatus()){
                    reportViewModel.getReportsAsync()
                    reportViewModel.getZonesAzync()
                }
                changeFragment(R.id.fragment_map, fragmentMap)
        }
    }

    private fun showSignInOptions(){
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.OpcionesInicioSesion)
            .build(),MY_REQUEST_CODE)
    }

    private fun changeFragment(id: Int, frag: Fragment){ supportFragmentManager.beginTransaction().replace(id, frag).commit() }

    override fun newReportClick(latLng: LatLng, idZone : String, level: Int) {
        var bundle = Bundle()
        bundle.putParcelable(AppConstants.LATLNT_KEY, latLng)
        bundle.putString(AppConstants.ZONE_KEY, idZone)
        bundle.putInt(AppConstants.LEVEL_KEY, level)
        startActivityForResult(Intent(this, NewReportActivity::class.java).putExtras(bundle), AppConstants.REQUEST_CODE)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if(saveFragment.getString(AppConstants.SAVE_FRAGMENT, "") != ""){
                if(saveFragment.getString(AppConstants.SAVE_FRAGMENT, "") == "3")
                    changeFragment(R.id.fragment_map, zonesFragment)
                else{
                    saveFragment.edit().putString(AppConstants.SAVE_FRAGMENT, "").apply()
                    changeFragment(R.id.fragment_map, fragmentMap)
                    map.isChecked = true
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if(auth.currentUser != null){
            correo_en_nav.text = auth.currentUser!!.email
            myReports.isVisible = true
            nombre_en_nav.text = auth.currentUser!!.displayName
            Glide.with(this).load(auth.currentUser!!.photoUrl).apply(RequestOptions.circleCropTransform()).into(imageView)
        }
        else myReports.isVisible = false
        if(saveTheme.getString(AppConstants.SAVE_THEME, "") == "1"){
            dark.isVisible = false
            bright.isVisible = true
        }
        if(saveFragment.getString(AppConstants.SAVE_FRAGMENT, "") != ""){
            dark.isVisible = false
            bright.isVisible = false
        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_log_in -> {
                showSignInOptions()
            }
            R.id.nav_log_out ->{
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        Toast.makeText(this, getString(R.string.logOut_successful), Toast.LENGTH_LONG).show()
                        correo_en_nav.text = getString(R.string.user_not_logged_in)
                        nombre_en_nav.text = getString(R.string.user_nav)
                        logIn.isVisible = true
                        logout.isVisible = false
                        myReports.isVisible = false
                        imageView.setImageResource(R.drawable.user_icon_nav)
                        if (saveTheme.getString(AppConstants.SAVE_THEME, "") == "0"){
                            dark.isVisible = true
                            bright.isVisible = false
                            changeFragment(R.id.fragment_map, fragmentMap)
                        }
                        else{
                            dark.isVisible = false
                            bright.isVisible = true
                            changeFragment(R.id.fragment_map, fragmentMap)
                        }
                    }
            }
            R.id.nav_dark_mode -> {
                if (saveTheme.getString(AppConstants.SAVE_THEME, "") == "0"){
                    saveTheme.edit().putString(AppConstants.SAVE_THEME, "1").apply()
                    fragmentMap.setMapTheme()
                    dark.isVisible = false
                    bright.isVisible = true
                }
            }
            R.id.nav_bright_mode -> {
                if (saveTheme.getString(AppConstants.SAVE_THEME, "") == "1"){
                    saveTheme.edit().putString(AppConstants.SAVE_THEME, "0").apply()
                    fragmentMap.setMapTheme()
                    dark.isVisible = true
                    bright.isVisible = false
                }
            }
            R.id.nav_my_reports -> {
                saveFragment.edit().putString(AppConstants.SAVE_FRAGMENT, "0").apply()
                if(!reportsFragment.isVisible)
                    changeFragment(R.id.fragment_map, reportsFragment)
                else reportsFragment.changeList()
                dark.isVisible = false
                bright.isVisible = false
            }
            R.id.nav_all_reports -> {
                saveFragment.edit().putString(AppConstants.SAVE_FRAGMENT, "1").apply()
                if(!reportsFragment.isVisible)
                    changeFragment(R.id.fragment_map, reportsFragment)
                else reportsFragment.changeList()
                dark.isVisible = false
                bright.isVisible = false
            }
            R.id.nav_map -> {
                changeFragment(R.id.fragment_map, fragmentMap)
                saveFragment.edit().putString(AppConstants.SAVE_FRAGMENT, "").apply()
                if(saveTheme.getString(AppConstants.SAVE_THEME, "") == "0"){
                    dark.isVisible = true
                    bright.isVisible = false
                }
                else{
                    bright.isVisible = true
                    dark.isVisible = false
                }
            }
            R.id.nav_zone -> {
                saveFragment.edit().putString(AppConstants.SAVE_FRAGMENT, "2").apply()
                changeFragment(R.id.fragment_map, zonesFragment)
                dark.isVisible = false
                bright.isVisible = false
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
