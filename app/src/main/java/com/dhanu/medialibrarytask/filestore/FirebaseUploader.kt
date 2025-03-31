package com.dhanu.medialibrarytask.filestore

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class FirebaseUploader(private val context: Context) {
    private val storageRef = FirebaseStorage.getInstance().reference
    private val fileDao = AppDatabase.getDatabase(context).fileDao()

    suspend fun uploadFilesToFirebase() {
        val files = fileDao.getUnUploadedFiles() // Get files that are not uploaded
        for (file in files) {
            val fileUri = Uri.fromFile(File(file.filePath)) // Convert local file to URI
            val fileRef = storageRef.child("uploads/${fileUri.lastPathSegment}") // Firebase path

            fileRef.putFile(fileUri).addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    CoroutineScope(Dispatchers.IO).launch {
                        fileDao.updateFileUrl(file.id, uri.toString()) // Update RoomDB with Firebase URL
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("FirebaseUploader", "Upload Failed: ${e.message}")
            }
        }
    }
}