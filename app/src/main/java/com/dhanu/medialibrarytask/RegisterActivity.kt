package com.dhanu.medialibrarytask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.dhanu.medialibrarytask.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("DARK_MODE", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }


        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Redirect to LoginActivity when "Already have an account? Sign In" is clicked
        binding.txtSignIn.setOnClickListener {
            val intent = Intent(this,LoginActivity:: class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.registerEmail.text.toString().trim()
            val password = binding.registerPassword.text.toString().trim()
            val confirmPassword = binding.registerConfirmPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if (password == confirmPassword){
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            firebaseAuth.signOut()
                            Toast.makeText(this,"User Successfully Registered", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this,LoginActivity:: class.java)
                            startActivity(intent)
                            finish() // Close RegisterActivity to prevent going back
                        }else{
                            Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                            Log.e("FirebaseAuth", "Error: ${task.exception?.message}")
                        }
                    }
                }else{
                    Toast.makeText(this,"Password is not match", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"All fields are required!", Toast.LENGTH_SHORT).show()
            }

        }
    }
}