package com.example.mytodolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.mytodolist.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityLoginBinding
    private val RC_SIGN_IN = 9001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        auth = Firebase.auth
        database = Firebase.database.reference

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.loginButton.setOnClickListener { view: View ->
        val email = binding.editTextTextEmailAddress.text.toString()
        val password = binding.editTextTextPassword.text.toString()
        signInUser(email, password)
        }

        binding.registerHere.setOnClickListener {
            Log.i("LoginActivity", "Register Here Clicked")
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.signInButton.setOnClickListener{
            Log.i("LoginActivity", "Google Sign In Clicked")
            signIn()
         
        }

    }

    //Sign In explicaitly by email and password.
        private fun signInUser(email:String, password: String){

        if (email == "") {
            binding.editTextTextEmailAddress.error = "Please Enter Your Email Address"
            binding.editTextTextEmailAddress.requestFocus()
            return
        }
        if (password == "") {
            binding.editTextTextPassword.error = "Please Enter Your Password"
            binding.editTextTextPassword.requestFocus()
            return
        }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                    {
                        // Sign in success, update UI with the signed-in user's information
                        Log.i("LoginActivity", "signInWithEmail:success")
                        Toast.makeText(this, "You are successfully logged in", Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser
                        onAuthSuccess(task.result?.user!!)
                       updateUI(user)
                    } else
                    { // If sign in fails, display a message to the user.
                        Log.i("LoginActivity", "signInWithEmail:failure", task.exception)
                        Toast.makeText(this, "Authentication failed. Try Again ${task.exception}", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }


        public override fun onStart() {
            super.onStart()
            val currentUser = auth.currentUser
            updateUI(currentUser)

        }

        private fun updateUI(currentUser: FirebaseUser?){
        if(currentUser != null){
            Log.i("LoginActivity", "Update UI Called")
            val intent = Intent(this, TodoActivity::class.java)
            startActivity(intent)
            finish()
                }}




      private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

//        Snackbar.make(
//            findViewById(android.R.id.content),
//            "You are successfully logged in",
//            Snackbar.LENGTH_SHORT
//        ).show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.i("LoginActivity", "Google sign in success. firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.i("LoginActivity", "Google sign in failed", e)
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.i("LoginActivity","firebaseAuthWithGoogle-signInWithCredential:success")

                    val user = auth.currentUser
                    onAuthSuccess(task.result?.user!!)
                    updateUI(user)
                    val intent = Intent(this, TodoActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.i("LoginActivity", "firebaseAuthWithGoogle-signInWithCredential:failure", task.exception)
                    // ...

                    Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.authentication_failed),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }

                // ...
            }
    }

    private fun writeNewUser(userId: String, name: String, email: String?) {
        val user = User(name, email)
        database.child("users").child(userId).setValue(user)
    }


    private fun usernameFromEmail(email: String): String {
        return if (email.contains("@")) {
            email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            email
        }
    }

    private fun onAuthSuccess(user: FirebaseUser) {
        val username = usernameFromEmail(user.email!!)
        // Write new user
        writeNewUser(user.uid, username, user.email)


    }

    }


data class User(
    var username: String? = "",
    var email: String? = ""
)









