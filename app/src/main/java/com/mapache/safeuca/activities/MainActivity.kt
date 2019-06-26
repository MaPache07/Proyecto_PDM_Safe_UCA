package com.mapache.safeuca.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.mapache.safeuca.R
import com.mapache.safeuca.database.entities.Zone
import com.mapache.safeuca.database.viewmodels.ReportViewModel
import com.mapache.safeuca.fragments.MapsFragment
import com.mapache.safeuca.fragments.ReportsFragment
import com.mapache.safeuca.utilities.AppConstants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MapsFragment.newReportClick, MapsFragment.changeTheme {

    private lateinit var fragmentMap : MapsFragment
    private lateinit var auth: FirebaseAuth
    private lateinit var reportsFragment: ReportsFragment
    lateinit var providers : List<AuthUI.IdpConfig>
    private lateinit var reportViewModel : ReportViewModel
    val MY_REQUEST_CODE : Int = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState != null){
            tv_escondido.text = savedInstanceState.getString(AppConstants.SAVE_THEME)
        }

        auth = FirebaseAuth.getInstance()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        FirebaseApp.initializeApp(this@MainActivity)
        reportViewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)

        Log.d("Hola", "OnCreate MainActivity")

        providers = Arrays.asList<AuthUI.IdpConfig>(
            AuthUI.IdpConfig.EmailBuilder().build(),//email login
            //AuthUI.IdpConfig.FacebookBuilder().build(), //fb login
            AuthUI.IdpConfig.GoogleBuilder().build()) //google login

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        val logIn = nav_view.menu[0]
        val map = nav_view.menu[1]
        val myReports = nav_view.menu[2]
        val allReports = nav_view.menu[3]
        val dark = nav_view.menu[4]
        val bright = nav_view.menu[5]
        val logout = nav_view.menu[6]
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
        changeTheme()
        if(auth.currentUser != null){
            val user = FirebaseAuth.getInstance().currentUser
            Toast.makeText(this, user!!.email, Toast.LENGTH_SHORT).show()
            logIn.isVisible = false
            logout.isVisible = true
            myReports.isVisible = true
            allReports.isVisible = true
            dark.isVisible = true
            bright.isVisible = false
        }
        map.isVisible = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MY_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            val logIn = nav_view.menu[0]
            val map = nav_view.menu[1]
            val myReports = nav_view.menu[2]
            val allReports = nav_view.menu[3]
            val dark = nav_view.menu[4]
            val bright = nav_view.menu[5]
            val logout = nav_view.menu[6]
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(this, "Iniciado correctamente", Toast.LENGTH_LONG).show()
                correo_en_nav.text = user!!.email
                nombre_en_nav.text = user!!.displayName
                logIn.isVisible = false
                logout.isVisible = true
                myReports.isVisible = true
                allReports.isVisible = true

                // ...
            } else {
                Toast.makeText(this, "Error al iniciar " + response, Toast.LENGTH_LONG).show()
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
        if(requestCode == AppConstants.REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                Log.d("Hola", "Entro")
                changeTheme()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(AppConstants.SAVE_THEME, tv_escondido.text.trim().toString())
    }

    private fun showSignInOptions(){
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.OpcionesInicioSesion)
            .build(),MY_REQUEST_CODE)
    }

    override fun changeTheme(){
        fragmentMap = MapsFragment.newInstance()
        changeFragment(R.id.fragment_map, fragmentMap)
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
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        val logIn = nav_view.menu[0]
        val map = nav_view.menu[1]
        val myReports = nav_view.menu[2]
        val allReports = nav_view.menu[3]
        val dark = nav_view.menu[4]
        val bright = nav_view.menu[5]
        val logout = nav_view.menu[6]
        if(auth.currentUser != null){
            correo_en_nav.text = auth.currentUser!!.email
            allReports.isVisible = true
            myReports.isVisible = true
            nombre_en_nav.text = auth.currentUser!!.displayName
            dark.isVisible = true
        }
        else{
            allReports.isVisible = true
            myReports.isVisible = false
            dark.isVisible = true
        }
        return true
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }*/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val logIn = nav_view.menu[0]
        val map = nav_view.menu[1]
        val myReports = nav_view.menu[2]
        val allReports = nav_view.menu[3]
        val dark = nav_view.menu[4]
        val bright = nav_view.menu[5]
        val logout = nav_view.menu[6]
        when (item.itemId) {
            R.id.nav_log_in -> {
                showSignInOptions()
            }
            R.id.nav_log_out ->{
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        Toast.makeText(this, "Cerrado correctamente", Toast.LENGTH_LONG).show()
                        correo_en_nav.text = "No hay sesión iniciada"
                        nombre_en_nav.text = "Usuario"
                        logIn.isVisible = true
                        map.isVisible = false
                        logout.isVisible = false
                        myReports.isVisible = false
                        allReports.isVisible = true
                        if (tv_escondido.text == "0"){
                            dark.isVisible = true
                            bright.isVisible = false
                            changeTheme()
                        }
                        else{
                            dark.isVisible = false
                            bright.isVisible = true
                            changeTheme()
                        }
                    }
            }
            R.id.nav_dark_mode -> {
                if (tv_escondido.text == "0"){
                    tv_escondido.text = "1"
                    changeTheme()
                    dark.isVisible = false
                    bright.isVisible = true
                }
            }
            R.id.nav_bright_mode -> {
                if (tv_escondido.text == "1"){
                    tv_escondido.text = "0"
                    changeTheme()
                    dark.isVisible = true
                    bright.isVisible = false
                }
            }
            R.id.nav_my_reports -> {
                reportsFragment = ReportsFragment.newInstance()
                changeFragment(R.id.fragment_map, reportsFragment)
                tv_escondido2.text = "0"
                dark.isVisible = false
                bright.isVisible = false
                map.isVisible = true
            }
            R.id.nav_all_reports -> {
                reportsFragment = ReportsFragment.newInstance()
                changeFragment(R.id.fragment_map, reportsFragment)
                tv_escondido2.text = "1"
                dark.isVisible = false
                bright.isVisible = false
                map.isVisible = true
            }
            R.id.nav_map -> {
                changeTheme()
                if(tv_escondido.text == "0"){
                    dark.isVisible = true
                    bright.isVisible = false
                }
                else{
                    bright.isVisible = true
                    dark.isVisible = false
                }
                if (auth.currentUser != null){
                    allReports.isVisible = true
                    myReports.isVisible = true
                }
                else{
                    allReports.isVisible = true
                    myReports.isVisible = false
                }
                map.isVisible = false
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
