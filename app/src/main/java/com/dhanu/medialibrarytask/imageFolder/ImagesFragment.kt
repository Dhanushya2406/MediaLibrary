package com.dhanu.medialibrarytask.imageFolder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dhanu.medialibrarytask.R

class ImagesFragment : Fragment() {

    /*private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageListAdapter
    private lateinit var mediaDatabase: RecentMediaDatabase
    private val imageList = mutableListOf<RecentMediaEntity>()*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_images, container, false)

        /*recyclerView = view.findViewById(R.id.imageRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3) // 3 columns
        imageAdapter = ImageListAdapter(imageList)
        recyclerView.adapter = imageAdapter

        mediaDatabase = RecentMediaDatabase.getDatabase(requireContext())
        loadImages()*/

        return view
    }

    /*private fun loadImages() {
        lifecycleScope.launch {
            imageList.clear()
            imageList.addAll(mediaDatabase.recentMediaDao().getImages())
            imageAdapter.notifyDataSetChanged()
        }
    }*/

}