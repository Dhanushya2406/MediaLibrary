package com.dhanu.medialibrarytask.filestore

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class FirebaseUploader(private val context: Context) {

    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    suspend fun uploadFilesToFirebase(fileUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                // Get file extension dynamically
                val fileType = getFileExtension(fileUri) ?: "unknown"

                // Check if file exists before uploading
                context.contentResolver.openInputStream(fileUri)?.use {
                    val fileRef = storageReference.child("media/${System.currentTimeMillis()}.$fileType")

                    // Start Upload
                    fileRef.putFile(fileUri)
                        .addOnSuccessListener { taskSnapshot ->
                            fileRef.downloadUrl.addOnSuccessListener { uri ->
                                onSuccess(uri.toString()) // Return Firebase URL
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("FirebaseUploader", "Upload failed: ${exception.message}")
                            onFailure(exception)
                        }
                } ?: run {
                    Log.e("FirebaseUploader", "File does not exist or is inaccessible: $fileUri")
                    onFailure(IOException("File does not exist"))
                }
            } catch (e: Exception) {
                Log.e("FirebaseUploader", "Error uploading file: ${e.message}")
                onFailure(e)
            }
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        return context.contentResolver.getType(uri)?.let { mimeType ->
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        }
    }
}