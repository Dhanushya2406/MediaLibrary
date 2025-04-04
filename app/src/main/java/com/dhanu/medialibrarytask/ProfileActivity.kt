package com.dhanu.medialibrarytask

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var switchTheme: SwitchCompat
    private lateinit var backButton: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE) // Initialize here
        val isDarkMode = sharedPreferences.getBoolean("DARK_MODE", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        setContentView(R.layout.activity_profile)

        backButton = findViewById(R.id.back_button)
//        backButton.setColorFilter(ContextCompat.getColor(this, R.color.back_arrow_color))

        switchTheme = findViewById(R.id.change_mode_toggle)
        switchTheme.isChecked = isDarkMode

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("DARK_MODE", isChecked).apply()
            // Apply theme change
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            // Restart activity to apply theme change
            delegate.applyDayNight()
        }

        val btnLogout = findViewById<TextView>(R.id.profile_logout)

        btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun logoutUser() {
        // Reset Dark Mode to Light Mode on Logout
        sharedPreferences.edit().putBoolean("DARK_MODE", false).apply()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        delegate.applyDayNight()

        FirebaseAuth.getInstance().signOut()
        // Navigate to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showLogoutDialog(){
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout"){_,_ ->
                logoutUser()
            }
            .setNegativeButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}

