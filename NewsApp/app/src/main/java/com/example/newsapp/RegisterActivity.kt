package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

/**
 * Class for creating the profiles
 */
class RegisterActivity : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.register_layout)

        val register = findViewById<EditText>(R.id.email_field)
        val passwordField = findViewById<EditText>(R.id.password_register)
        val button = findViewById<AppCompatButton>(R.id.register_button_layout)

        //Register button with textfield validation
        button.setOnClickListener { view ->

            if (!register.text.toString().isEmpty() && !passwordField.text.toString().isEmpty()) {
                mAuth.createUserWithEmailAndPassword(
                    register.text.toString(),
                    passwordField.text.toString()
                ).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val intent = Intent(this, Loginpageactivity::class.java)

                        mAuth.currentUser?.sendEmailVerification()//Sends the verification to the user email

                        Toast.makeText(
                            this,
                            "Email Verification sent to " + mAuth.currentUser?.email.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        startActivity(intent)
                    } else {
                        Snackbar.make(view, "Cannot be null ", Snackbar.LENGTH_LONG).show()
                    }
                }

            } else {
                Snackbar.make(view, "Cannot be null ", Snackbar.LENGTH_LONG).show()
            }
        }


    }


}