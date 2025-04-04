package com.dhanu.medialibrarytask

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.dhanu.medialibrarytask.allFolder.AllFragment
import com.dhanu.medialibrarytask.databinding.ActivityLoginBinding
import com.dhanu.medialibrarytask.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        // Force light mode in LoginActivity
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState) // Now call super after setting the theme

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.txtRegister.setOnClickListener {
            val intent = Intent(this,RegisterActivity:: class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding. loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            // Authenticate User with Email and Password using Firebase
            if (email.isNotEmpty() && password.isNotEmpty()){
                    firebaseAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener {
                        if (it.isSuccessful){
                            val intent = Intent(this,MediaDashboardActivity:: class.java)
                            Toast.makeText(this,"User Successfully Login", Toast.LENGTH_SHORT).show()
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            }else{
                Toast.makeText(this,"Empty fields are not Allowed!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null){
            val intent = Intent(this,MediaDashboardActivity:: class.java)
            startActivity(intent)
            finish()
        }
    }

}