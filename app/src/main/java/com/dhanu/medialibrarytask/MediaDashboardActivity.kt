package com.dhanu.medialibrarytask

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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



    private lateinit var recentMediaListAdapter: RecentMediaListAdapter
    private var mediaList: MutableList<RecentMediaEntity> = mutableListOf()



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







        // Initialize RecyclerView Adapter with empty list
        recentMediaListAdapter = RecentMediaListAdapter(mediaList)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MediaDashboardActivity)
            adapter = recentMediaListAdapter
            visibility = View.GONE  // Initially hidden
        }

        binding.noResultsTextView.visibility = View.GONE

        // Load the data into adapter (This is the fix!)
        loadRecentMediaList()

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                val isSearching = query.isNotEmpty()

                // Show/Hide TabLayout based on search state
                binding.tabLayout.visibility = if (isSearching) View.GONE else View.VISIBLE
                binding.fragmentContainer.visibility = if (isSearching) View.GONE else View.VISIBLE // Hides Folders & Recent section

                recentMediaListAdapter.filter(query)
                binding.recyclerView.visibility = if (isSearching) View.VISIBLE else View.GONE
                binding.noResultsTextView.visibility = if (isSearching && recentMediaListAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })





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

        // Load Default Fragment (All Media)
        replaceFragment(AllFragment())

        // Handle tab selection
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.searchEditText.text?.clear() // Clear search when switching tabs
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
            .addToBackStack(null) // Enables back navigation between tabs
            .commit()
    }

    private fun loadRecentMediaList() {
        // Whatever media files are added will be included in the RecyclerView
        recentMediaListAdapter.updateList(mediaList)

        Log.d("FilterDebug", "Media list updated, size: ${mediaList.size}")
    }


}