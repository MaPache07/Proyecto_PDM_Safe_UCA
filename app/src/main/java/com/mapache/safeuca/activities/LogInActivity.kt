package com.mapache.safeuca.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mapache.safeuca.R
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG : String = "Sesion"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        auth = FirebaseAuth.getInstance()
        log_in_bt.setOnClickListener {
            val mail : String = log_in_mail.text.toString()
            val psswd : String = log_in_psswd.text.toString()
            if (mail==""||psswd==""){
                Toast.makeText(this,"No pueden haber campos vacios", Toast.LENGTH_LONG).show()
            }
            else{
                auth.signInWithEmailAndPassword(mail, psswd)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            Toast.makeText(baseContext, "Inicio de sesion exitoso " + auth.currentUser!!.email, Toast.LENGTH_SHORT).show()
                            val user = auth.currentUser
                            //updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
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
