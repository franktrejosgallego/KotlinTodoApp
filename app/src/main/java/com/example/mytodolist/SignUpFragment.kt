package com.example.mytodolist

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mytodolist.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSignUpBinding

    //----------------------- OnCreate Start
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate<FragmentSignUpBinding>(inflater, R.layout.fragment_sign_up, container, false)
        auth = Firebase.auth

        binding.signUpButton.setOnClickListener{view: View ->
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()

         signUpUser(email,password)

        }

        binding.alreadyRegistered.setOnClickListener{
            view?.findNavController()?.navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        binding.selectPhotoButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK )
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

//        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//            super.onActivityResult(requestCode, resultCode, data)
//            if(requestCode==0 && resultCode == Activity.RESULT_OK && data != null){
//                Log.d("SignUp", "Photo Selected")
//
//                val uri = data.data
//                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
//            }
//        }

        return binding.root
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
                 Log.d("Login", "Account Created, Hurray!! id is ${task.result?.user?.uid} + $email")
                    Toast.makeText(context, "Authentication Successful.",
                        Toast.LENGTH_SHORT).show()

                    findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    fun updateUI(currentUser: FirebaseUser?){

    }


}