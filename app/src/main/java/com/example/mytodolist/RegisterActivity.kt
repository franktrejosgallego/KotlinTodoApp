package com.example.mytodolist

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.*
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.mytodolist.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)

        auth = Firebase.auth

        binding.signUpButton.setOnClickListener{view: View ->
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()

            signUpUser(email,password)

        }

        binding.alreadyRegistered.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.selectPhotoButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK )
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    private fun signUpUser(email: String, password: String) {
        if (email == "") {
            binding.editTextTextEmailAddress.error = "Please Enter Your Email Address"
            binding.editTextTextEmailAddress.requestFocus()
            return
        }
        if (password == "") {
            binding.editTextTextPassword.error = "Please Enter Your Email Address"
            binding.editTextTextPassword.requestFocus()
            return
        }


        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{task->
                if (task.isSuccessful) {
                    d("Login", "Account Created, Hurray!! id is ${task.result?.user?.uid} + $email")
                    Toast.makeText(this, "Authentication Successful.",
                        Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    fun updateUI(currentUser: FirebaseUser?){

    }
}