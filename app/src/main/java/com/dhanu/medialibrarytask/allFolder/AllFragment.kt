package com.dhanu.medialibrarytask.allFolder

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhanu.medialibrarytask.AudioFragment
import com.dhanu.medialibrarytask.DocumentsFragment
import com.dhanu.medialibrarytask.imageFolder.ImagesFragment
import com.dhanu.medialibrarytask.R
import com.dhanu.medialibrarytask.VideosFragment
import com.dhanu.medialibrarytask.filestore.AppDatabase
import com.dhanu.medialibrarytask.filestore.FileDao
import com.dhanu.medialibrarytask.filestore.FileEntity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class AllFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FolderListAdapter
    private val folderList = mutableListOf<FolderItem>()
    private lateinit var fileDao: FileDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all, container, false)

        fileDao = AppDatabase.getDatabase(requireContext()).fileDao()

        recyclerView = view.findViewById(R.id.folderRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3) // 3 columns

        folderList.apply {
            add(FolderItem("Cyber Nexus", 0))
            add(FolderItem("Product Development", 1))
            add(FolderItem("Vendor Contracts", 2))
        }
        adapter = FolderListAdapter(folderList)
        recyclerView.adapter = adapter

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
            val filePath = getRealPathFromUri(requireContext(), it)
            if (filePath != null) {
                saveFileToRoomDB(filePath)
                Toast.makeText(requireContext(), "File saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to get file path", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveFileToRoomDB(filePath: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val fileEntity = FileEntity(filePath = filePath, firebaseUrl = null)
            fileDao.insertFile(fileEntity)
        }
    }

    private fun getRealPathFromUri(context: Context, uri: Uri): String? {
        var result: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val idx = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                if (idx != -1) {
                    result = it.getString(idx)
                }
            }
        }
        return result ?: uri.path
    }

    companion object {
        private const val FILE_PICKER_REQUEST_CODE = 1001
    }
}