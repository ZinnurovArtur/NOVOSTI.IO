package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

/**
 * Login page validation of user credentials
 */
class Loginpageactivity : AppCompatActivity() {
    private var mAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val RC_SIGN_IN = 120 // Sign in code
    }

    /**
     * initialise elements of layout and initialise Google services
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginpage)

        val loginfield = findViewById<EditText>(R.id.login_field)
        val passwordField = findViewById<EditText>(R.id.text_input_password_toggle)

        val button = findViewById<AppCompatButton>(R.id.button_login)
        val registerButton = findViewById<AppCompatButton>(R.id.button_register)
        val googlebutton = findViewById<SignInButton>(R.id.google_button)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        googleSignInClient = GoogleSignIn.getClient(this, gso)


        button.setOnClickListener { view ->

            if (!loginfield.text.toString().isEmpty() && !passwordField.text.toString().isEmpty()) {

                mAuth.signInWithEmailAndPassword(
                    loginfield.text.toString(),
                    passwordField.text.toString()
                ).addOnCompleteListener(
                    this

                ) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        /**
                         * for email verification uncomment because email shoudl be real
                         *
                         *
                        if (user!!.isEmailVerified) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        }
                        else{
                        Toast.makeText(this, "Email is not verified", Toast.LENGTH_LONG).show()
                        }
                         **/
                    } else {
                        Snackbar.make(view, "Something went wrong", Snackbar.LENGTH_LONG).show()
                    }
                }
            } else {
                Snackbar.make(view, "Cannot be null ", Snackbar.LENGTH_LONG).show()
            }

        }


        // start register activity to start registration
        registerButton.setOnClickListener { view ->

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)


        }

        //Sign in by google account button
        googlebutton.setOnClickListener { view ->
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)


        }


    }

    /**
     * Asynchronous  call to get result of registration
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("auth", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("auth", "Google sign in failed", e)

                }
            } else {
                Log.w("auth", "Google sign in failed")
            }

        }
    }

    /**
     *  Sign in by Google
     *  @param idToken unique device token
     *
     *  This implementation was made by Firebase documentation
     *
     */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("auth", "signInWithCredential:success")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("auth", "signInWithCredential:failure", task.exception)
                }

            }
    }


}