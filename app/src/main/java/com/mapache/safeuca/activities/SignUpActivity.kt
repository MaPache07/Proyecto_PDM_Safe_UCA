package com.mapache.safeuca.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mapache.safeuca.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.nav_header_main.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG : String = "Sesion"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        sign_up_bt.setOnClickListener {
            val mail : String = sign_up_mail.text.toString()
            val psswd : String = sign_up_psswd.text.toString()
            if (mail == ""|| psswd == ""){
                Toast.makeText(this,"No pueden haber campos vacios", Toast.LENGTH_LONG).show()
            }
            else{
                auth.createUserWithEmailAndPassword(mail, psswd)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                val user = auth.currentUser
                                Toast.makeText(this,"Creado "+user!!.email+"con exito", Toast.LENGTH_LONG).show()
                                //val user = auth.currentUser
                                //updateUI(auth.currentUser)
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                                //updateUI(null)
                            }

                            // ...
                        }
            }
        }
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        //updateUI(currentUser)
    }

    fun updateUI(user: FirebaseUser?){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("currentUser", user)
        startActivity(intent)
    }
}
