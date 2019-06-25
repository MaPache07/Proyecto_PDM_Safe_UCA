package com.mapache.safeuca.activities

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
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
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MapsFragment.newReportClick, MapsFragment.changeTheme {

    private lateinit var fragmentMap : MapsFragment
    private lateinit var auth: FirebaseAuth
    private lateinit var reportsFragment: ReportsFragment

    lateinit var providers : List<AuthUI.IdpConfig>
    val MY_REQUEST_CODE : Int = 123
    var flag : String = "0"

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
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
        changeTheme()
        if(auth.currentUser != null){
            val user = FirebaseAuth.getInstance().currentUser
            Toast.makeText(this, user!!.email, Toast.LENGTH_SHORT).show()
            if (correo_en_nav == null){
                Log.d("gol","cualquier mensaje")
            }
            navView.menu[0].isVisible = false
            navView.menu[2].isVisible = true
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MY_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(this, "Iniciado correctamente", Toast.LENGTH_LONG).show()
                correo_en_nav.text = user!!.email
                nav_view.menu[0].isVisible = false
                nav_view.menu[2].isVisible = true
                nav_view.menu[5].isVisible = true
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
        menuInflater.inflate(R.menu.main, menu)
        if(auth.currentUser != null){
            correo_en_nav.text = auth.currentUser!!.email
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
        when (item.itemId) {
            R.id.nav_log_in -> {
                showSignInOptions()
            }
            R.id.nav_settings -> {

            }
            R.id.nav_log_out ->{
                correo_en_nav.text = "No hay seesión iniciada"
                nav_view.menu[0].isVisible = true
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        // ...
                        Toast.makeText(this, "Cerrado correctamente", Toast.LENGTH_LONG).show()
                    }
                nav_view.menu[2].isVisible = false
            }
            R.id.nav_dark_mode -> {
                if (flag == "0"){
                    tv_escondido.text = "1"
                    changeTheme()
                    flag = "1"
                    nav_view.menu[3].isVisible = false
                    nav_view.menu[4].isVisible = true
                }
            }
            R.id.nav_bright_mode -> {
                if (flag == "1"){
                    tv_escondido.text = "0"
                    changeTheme()
                    flag = "0"
                    nav_view.menu[3].isVisible = true
                    nav_view.menu[4].isVisible = false
                }
            }
            R.id.nav_reports -> {
                reportsFragment = ReportsFragment.newInstance()
                changeFragment(R.id.fragment_map, reportsFragment)
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
