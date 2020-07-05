package com.example.mytodolist.login

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.mytodolist.R
import com.example.mytodolist.databinding.ActivityMainBinding
import com.example.mytodolist.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    //----------------------- OnCreate Start
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater, R.layout.fragment_login, container, false)
        auth = Firebase.auth

        binding.loginButton.setOnClickListener{view: View ->
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()
            Log.i("Login", "Email is $email")
            Log.i("Login", "Email is $password")
            Log.i("Login", "You are successfully logged in")
            Toast.makeText(context, "You are successfully loggeg in", Toast.LENGTH_SHORT).show()

                signInUser(email,password)
        }

        binding.registerHere.setOnClickListener{view: View ->
            view.findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        return binding.root
    }
    //------------------------ OnCreate End

    private fun signInUser(email:String, password: String){

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed. Try Again",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                    // ...
                }

                // ...
            }

    }



    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    fun updateUI(currentUser: FirebaseUser?){

    }
}