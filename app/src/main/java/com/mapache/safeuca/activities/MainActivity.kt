package com.mapache.safeuca.activities

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.Visibility
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.mapache.safeuca.R
import com.mapache.safeuca.fragments.MapsFragment
import com.mapache.safeuca.fragments.ReportsFragment
import com.mapache.safeuca.utilities.AppConstants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MapsFragment.newReportClick, MapsFragment.changeTheme {

    private lateinit var fragmentMap : MapsFragment
    private lateinit var auth: FirebaseAuth
    private lateinit var reportsFragment: ReportsFragment
    lateinit var providers : List<AuthUI.IdpConfig>
    val MY_REQUEST_CODE : Int = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        FirebaseApp.initializeApp(this@MainActivity)

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

    /*override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        } else {
            write()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                write()
            }
        }
    }

    private fun write() {
        val dir = "${Environment.getExternalStorageDirectory()}/$packageName"
        File(dir).mkdirs()
        val file = "%1\$tY%1\$tm%1\$td%1\$tH%1\$tM%1\$tS.log".format(Date())
        File("$dir/$file").printWriter().use {
            it.println("text")
        }
    }*/

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
    }

    private fun showSignInOptions(){
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.OpcionesInicioSesion)
            .build(),MY_REQUEST_CODE)
    }

    override fun changeTheme(){
        fragmentMap = MapsFragment.newInstance(BottomSheetDialog(this))
        changeFragment(R.id.fragment_map, fragmentMap)
    }

    private fun changeFragment(id: Int, frag: Fragment){ supportFragmentManager.beginTransaction().replace(id, frag).commit() }

    override fun newReportClick(latLng: LatLng, idZone : String, level: Int) {
        var bundle = Bundle()
        bundle.putParcelable(AppConstants.LATLNT_KEY, latLng)
        bundle.putString(AppConstants.IDZONE_KEY, idZone)
        bundle.putInt(AppConstants.LEVEL_KEY, level)
        startActivity(Intent(this, NewReportActivity::class.java).putExtras(bundle))
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
        menuInflater.inflate(R.menu.main, menu)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

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
