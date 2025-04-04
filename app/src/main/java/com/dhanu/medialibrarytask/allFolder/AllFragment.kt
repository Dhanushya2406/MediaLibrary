package com.dhanu.medialibrarytask.allFolder

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhanu.medialibrarytask.R
import com.dhanu.medialibrarytask.filestore.AppDatabase
import com.dhanu.medialibrarytask.filestore.FileDao
import com.dhanu.medialibrarytask.filestore.FileEntity
import com.dhanu.medialibrarytask.filestore.FirebaseUploader
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FolderListAdapter
    private val folderList = mutableListOf<FolderItem>()

    private lateinit var mediaRecyclerView: RecyclerView
    private lateinit var recentAdapter: RecentMediaListAdapter
    private val mediaList = mutableListOf<RecentMediaEntity>()

    private lateinit var fileDao: FileDao
    private lateinit var firebaseUploader: FirebaseUploader

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all, container, false)

        fileDao = AppDatabase.getDatabase(requireContext()).fileDao()
        firebaseUploader = FirebaseUploader(requireContext())

        // Folder List Recycler view
        recyclerView = view.findViewById(R.id.folderRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3) // 3 columns

        folderList.apply {
            add(FolderItem("Cyber Nexus", 0))
            add(FolderItem("Product Development", 1))
            add(FolderItem("Vendor Contracts", 2))
        }
        adapter = FolderListAdapter(folderList)
        recyclerView.adapter = adapter


        // Initialize RecyclerView
        mediaRecyclerView = view.findViewById(R.id.recentMediaRecyclerView)
        mediaRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize Adapter with mediaList
        recentAdapter = RecentMediaListAdapter(mediaList)
        mediaRecyclerView.adapter = recentAdapter

        loadFilesFromRoomDB()   // Load media files

        //Floating Action Button functionality
        val addButton = view.findViewById<FloatingActionButton>(R.id.addButton)
        val addOptionsLayout = view.findViewById<LinearLayout>(R.id.addOptionsLayout)
        val createFolderButton = view.findViewById<ImageButton>(R.id.createFolderButton)
        val closeButton = view.findViewById<ImageButton>(R.id.closeButton)

        addButton.setOnClickListener {
            addOptionsLayout.visibility = View.VISIBLE
            addButton.visibility = View.GONE
        }

        closeButton.setOnClickListener {
            addOptionsLayout.visibility = View.GONE
            addButton.visibility = View.VISIBLE
        }

        createFolderButton.setOnClickListener {
            showCreateFolderDialog()
            addOptionsLayout.visibility = View.GONE
        }

        val mediaUploadBtn = view.findViewById<ImageButton>(R.id.upload_Button)

        mediaUploadBtn.setOnClickListener {
            pickFile()
        }
        return view
    }

    private fun showCreateFolderDialog() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottomsheetlayout, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bottomSheetView)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val folderNameEditText = bottomSheetView.findViewById<TextInputEditText>(R.id.etFolderName)
        val createButton = bottomSheetView.findViewById<Button>(R.id.btnCreate)
        val cancelButton = bottomSheetView.findViewById<Button>(R.id.btnCancel)

        cancelButton.setOnClickListener { dialog.dismiss() }

        createButton.setOnClickListener {
            val folderName = folderNameEditText?.text.toString().trim()
            if (folderName.isNotEmpty()) {
                folderList.add(FolderItem(folderName, 0))
                adapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Folder Successfully created", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Folder name cannot be empty", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
            view?.findViewById<FloatingActionButton>(R.id.addButton)?.visibility = View.VISIBLE
        }

        dialog.setOnDismissListener {
            view?.findViewById<FloatingActionButton>(R.id.addButton)?.visibility = View.VISIBLE
        }
        dialog.show()
    }

    private fun pickFile() {
        filePickerLauncher.launch("*/*")
    }

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            saveFileToRoomDB(it) // Save URI directly
            Toast.makeText(requireContext(), "File saved!", Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(requireContext(), "Failed to get file", Toast.LENGTH_SHORT).show()
    }

    private fun saveFileToRoomDB(fileUri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            // Insert the file URI into RoomDB first
            val insertedId = fileDao.insertFile(FileEntity(filePath = fileUri.toString()))

            if (insertedId > 0) {
                Log.d("AllFragment", "File inserted into RoomDB: ID = $insertedId")

                // Now switch to Main thread to update UI with the inserted URI (local)
                withContext(Dispatchers.Main) {
                    loadFilesFromRoomDB() // Show locally picked file immediately
                }

                // Then start Firebase upload in background
                firebaseUploader.uploadFilesToFirebase(
                    fileUri,
                    onSuccess = { downloadUrl ->
                        Log.d("FirebaseUploader", "File uploaded successfully: $downloadUrl")

                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                            fileDao.updateFileUrl(insertedId.toInt(), downloadUrl)
                            Log.d("AllFragment", "Download URL updated in RoomDB")
                        }
                    },
                    onFailure = { exception ->
                        Log.e("FirebaseUploader", "Upload failed: ${exception.message}")
                    }
                )
            } else {
                // Error inserting into Room
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to save file!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadFilesFromRoomDB() {
        lifecycleScope.launch(Dispatchers.IO) {
            val files = fileDao.getAllFiles() // Fetch all stored files from RoomDB
            Log.d("AllFragment", "Loaded ${files.size} files from RoomDB")

            val newMediaList = files.map { file ->
                val fileName = file.filePath.substringAfterLast("/")
                val fileType = getFileType(file.filePath)

                RecentMediaEntity(
                    filePath = file.filePath,
                    fileType = fileType,
                    fileName = fileName
                )
            }

            lifecycleScope.launch(Dispatchers.Main) {
                recentAdapter.updateList(newMediaList) // Efficient adapter update
                Log.d("AllFragment", "RecyclerView updated with ${newMediaList.size} items")
            }
        }
    }


    private fun getFileType(filePath: String): String {
        return when {
            filePath.endsWith(".jpg", true) || filePath.endsWith(".jpeg", true) || filePath.endsWith(".png", true) -> "Image"
            filePath.endsWith(".mp4", true) || filePath.endsWith(".mkv", true) || filePath.endsWith(".avi", true) -> "Video"
            filePath.endsWith(".mp3", true) || filePath.endsWith(".wav", true) -> "Audio"
            filePath.endsWith(".pdf", true) || filePath.endsWith(".doc", true) || filePath.endsWith(".docx", true) ||
                    filePath.endsWith(".xls", true) || filePath.endsWith(".xlsx", true) || filePath.endsWith(".ppt", true) ||
                    filePath.endsWith(".pptx", true) -> "Document"
            else -> "Other"
        }
    }

}