package com.dhanu.medialibrarytask

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhanu.medialibrarytask.allFolder.AllFragment
import com.dhanu.medialibrarytask.allFolder.RecentMediaEntity
import com.dhanu.medialibrarytask.allFolder.RecentMediaListAdapter
import com.dhanu.medialibrarytask.databinding.ActivityMediaDashboardBinding
import com.dhanu.medialibrarytask.imageFolder.ImagesFragment
import com.google.android.material.tabs.TabLayout

class MediaDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaDashboardBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("DARK_MODE", false)

        // Apply Dark Mode only if the user is logged in
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        binding = ActivityMediaDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load AllFragment where media files are displayed
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, AllFragment())
            .commit()

        // Add a Profile image for theme switching
        binding.imgProfile.setOnClickListener {
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }


        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("All"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Images"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Videos"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Documents"))

        // Load the default fragment
        replaceFragment(AllFragment())

        // Handle tab selection
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 -> replaceFragment(AllFragment())
                    1 -> replaceFragment(ImagesFragment())
                    2 -> replaceFragment(AudioFragment())
                    3 -> replaceFragment(VideosFragment())
                    4 -> replaceFragment(DocumentsFragment())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun replaceFragment(fragment: androidx.fragment.app.Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer,fragment)
            .commit()
    }
}