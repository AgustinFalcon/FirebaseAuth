package com.example.firebaseaguss

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.firebaseaguss.databinding.FragmentMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception



class MainFragment : Fragment() {

    lateinit var auth: FirebaseAuth


    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        _binding = FragmentMainBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance();
        //auth.signOut();

        binding.btnRegister.setOnClickListener {
            registerUser();
        }

        binding.btnLogin.setOnClickListener {
            loginUser();
        }

        binding.btnUpdateProfile.setOnClickListener {
            updateProfile();
        }


        return binding.root
    }

    private fun updateProfile() {
        auth.currentUser?.let { user ->
            val userName = binding.etUsername.text.toString()
            val photoURI = Uri.parse("android.resource://${context?.packageName}/${R.drawable.logo_argentina}")
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .setPhotoUri(photoURI)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdates).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                        Toast.makeText(context, "Successfully update user profile", Toast.LENGTH_LONG).show()
                    }

                }catch(e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


    private fun registerUser() {
        val email = binding.etEmailRegister.text.toString()
        val password = binding.etPasswordRegister.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        checkLoggedInState()
                    }
                }catch(e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun loginUser() {
        val email = binding.etEmailLogin.text.toString()
        val password = binding.etPasswordLogin.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        checkLoggedInState()
                    }
                }catch(e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun checkLoggedInState() {
        val user = auth.currentUser
        if(user == null){
            binding.tvLoggedIn.text = "You are not logged in"
        }else{
            binding.tvLoggedIn.text = "You are logged in"
            binding.etUsername.setText(user.displayName)
            binding.ivProfilePicture.setImageURI(user.photoUrl)
        }
    }


    override fun onStart() {
        super.onStart()
        checkLoggedInState();
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}