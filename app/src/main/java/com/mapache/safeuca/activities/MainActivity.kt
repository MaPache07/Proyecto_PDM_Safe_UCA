package com.mapache.safeuca.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.mapache.safeuca.R
import com.mapache.safeuca.fragments.MapsFragment
import com.mapache.safeuca.utilities.AppConstants
import kotlinx.android.synthetic.main.nav_header_main.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MapsFragment.newReportClick {

    private lateinit var fragmentMap : MapsFragment
    lateinit var providers : List<AuthUI.IdpConfig>
    val MY_REQUEST_CODE : Int = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

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

        fragmentMap = MapsFragment.newInstance(BottomSheetDialog(this))
        changeFragment(R.id.fragment_map, fragmentMap)

//////////////////////////////////////////////////////////////////////////////////////////////////////
        providers = Arrays.asList<AuthUI.IdpConfig>(
                AuthUI.IdpConfig.EmailBuilder().build(),//email login
                //AuthUI.IdpConfig.FacebookBuilder().build(), //fb login
                AuthUI.IdpConfig.GoogleBuilder().build()) //google login
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //val cerrar_sesion : Button = findViewById(R.id.nav_log_out)

        if (requestCode == MY_REQUEST_CODE){
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(this, ""+user!!.email,Toast.LENGTH_LONG).show()

                //cerrar_sesion.isEnabled=true
            }
            else{
                Toast.makeText(this, ""+response!!.error!!.message,Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun showSignInOptions(){
        FirebaseApp.initializeApp(this@MainActivity)
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.OpcionesInicioSesion)
                .build(),MY_REQUEST_CODE)
    }
////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun changeFragment(id: Int, frag: Fragment){ supportFragmentManager.beginTransaction().replace(id, frag).commit() }

    override fun newReportClick(latLng: LatLng, level: Int) {
        var bundle = Bundle()
        bundle.putParcelable(AppConstants.LATLNT_KEY, latLng)
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
            R.id.nav_home -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_tools -> {

            }
            R.id.nav_log_out ->{
                AuthUI.getInstance().signOut(this@MainActivity)
                        .addOnCompleteListener{
                            //showSignInOptions()
                        }
                        .addOnFailureListener {
                            e-> Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                        }
            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
