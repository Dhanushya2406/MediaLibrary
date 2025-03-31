package com.dhanu.medialibrarytask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dhanu.medialibrarytask.allFolder.AllFragment
import com.dhanu.medialibrarytask.databinding.ActivityMediaDashboardBinding
import com.dhanu.medialibrarytask.imageFolder.ImagesFragment
import com.google.android.material.tabs.TabLayout

class MediaDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("All"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Images"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Videos"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Documents"))

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, AllFragment())
            .commit()

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

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

    }

    private fun replaceFragment(fragment: androidx.fragment.app.Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer,fragment)
            .commit()
    }
}