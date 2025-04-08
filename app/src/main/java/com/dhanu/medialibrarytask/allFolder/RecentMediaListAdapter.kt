package com.dhanu.medialibrarytask.allFolder

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dhanu.medialibrarytask.PreviewActivity
import com.dhanu.medialibrarytask.R
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class RecentMediaListAdapter(private var mediaList: List<RecentMediaEntity>) :
    RecyclerView.Adapter<RecentMediaListAdapter.MediaViewHolder>() {

    private var filteredList: List<RecentMediaEntity> = mediaList.toList()

    class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileTypeIcon: ImageView = itemView.findViewById(R.id.fileTypeIcon)
        val fileName: TextView = itemView.findViewById(R.id.fileName)
        val optionsMenu: ImageView = itemView.findViewById(R.id.moreOptions)
    }

    // File type icons mapping
    private val fileTypeIcons = mapOf(
        "image" to R.drawable.ic_file_type_icon,
        "video" to R.drawable.ic_add_new_video_icon,
        "audio" to R.drawable.ic_add_new_audio_icon,
        "document" to R.drawable.ic_file_type_document_icon
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recent_card_media_list_item, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val media = filteredList[position]
        val decodePath = Uri.decode(media.filePath)

        val fileNameWithExt = decodePath.substringAfterLast("/")
        // Extract extension & convert to lowercase
        val fileExtension = fileNameWithExt.substringAfterLast(".", "").lowercase()
        Log.d("FilterDebug", "Displaying item: $fileNameWithExt at position $position")

        // Determine file type from extension
        val fileType = when (fileExtension) {
            "jpg", "jpeg", "png", "gif", "bmp", "webp" -> "image"
            "mp4", "mkv", "avi", "mov", "flv" -> "video"
            "mp3", "wav", "aac", "flac" -> "audio"
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt" -> "document"
            else -> "unknown" // Default case
        }
        holder.fileName.text = fileNameWithExt
        holder.fileTypeIcon.setImageResource(fileTypeIcons[fileType] ?: R.drawable.ic_add_new_image_icon)
        holder.optionsMenu.setOnClickListener { view ->
            val popupMenu = PopupMenu(holder.itemView.context, view)
            popupMenu.menuInflater.inflate(R.menu.options_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_preview -> {
                        openPreviewActivity(holder.itemView.context, decodePath)
                        true
                    }
                    R.id.action_download -> {
                        downloadFile(holder.itemView.context, media.filePath, fileNameWithExt)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    override fun getItemCount(): Int = filteredList.size

    fun updateList(newList: List<RecentMediaEntity>) {
        val diffCallback = MediaDiffCallback(filteredList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        mediaList = newList.toList()
        filteredList = newList.toList()
        diffResult.dispatchUpdatesTo(this)

        Log.d("AdapterDebug", "Media list updated, new size: ${mediaList.size}")
    }

    class MediaDiffCallback(
        private val oldList: List<RecentMediaEntity>,
        private val newList: List<RecentMediaEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].filePath == newList[newItemPosition].filePath
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    private fun openPreviewActivity(context: Context, filePath: String) {
        val uri = Uri.parse(filePath)
        val intent = Intent(context, PreviewActivity::class.java)
        intent.putExtra("file_uri", uri.toString()) // ✅ Updated to match PreviewActivity
        context.startActivity(intent)
    }



    // Download File from Firebase
    private fun downloadFile(context: Context, fileUrl: String, fileName: String) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl)
        val localFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)

        storageRef.getFile(localFile)
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Download Completed: ${localFile.absolutePath}",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Download Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            .addOnProgressListener { taskSnapshot ->
                val progress =
                    (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                Toast.makeText(context, "Downloading... $progress%", Toast.LENGTH_SHORT).show()
            }

    }

    /*// corrected one ever
    fun updateList(newList: List<RecentMediaEntity>) {
        mediaList = newList.toList()  // Store a copy of the full list
        filteredList = newList.toList()  // Reset filteredList
        Log.d("FilterDebug", "Media list updated, size: ${mediaList.size}")
        notifyDataSetChanged()
    }*/

    /*package com.dhanu.medialibrarytask.allFolder

    import android.net.Uri
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageView
    import android.widget.TextView
    import androidx.recyclerview.widget.RecyclerView
    import com.dhanu.medialibrarytask.R

    class RecentMediaListAdapter(private var mediaList: List<RecentMediaEntity>) :
        RecyclerView.Adapter<RecentMediaListAdapter.MediaViewHolder>() {

        private var filteredList: List<RecentMediaEntity> = mediaList.toList()

        class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val fileTypeIcon: ImageView = itemView.findViewById(R.id.fileTypeIcon)
            val fileName: TextView = itemView.findViewById(R.id.fileName)
        }

        // File type icons mapping
        private val fileTypeIcons = mapOf(
            "image" to R.drawable.ic_file_type_icon,
            "video" to R.drawable.ic_add_new_video_icon,
            "audio" to R.drawable.ic_add_new_audio_icon,
            "document" to R.drawable.ic_file_type_document_icon
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recent_card_media_list_item, parent, false)
            return MediaViewHolder(view)
        }

        override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
            val media = mediaList[position]
            val decodePath = Uri.decode(media.filePath)

            val fileNameWithExt = decodePath.substringAfterLast("/")
            // Extract extension & convert to lowercase
            val fileExtension = fileNameWithExt.substringAfterLast(".", "").lowercase()

            // Determine file type from extension
            val fileType = when (fileExtension) {
                "jpg", "jpeg", "png", "gif", "bmp", "webp" -> "image"
                "mp4", "mkv", "avi", "mov", "flv" -> "video"
                "mp3", "wav", "aac", "flac" -> "audio"
                "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt" -> "document"
                else -> "unknown" // Default case
            }
            holder.fileName.text = fileNameWithExt
            holder.fileTypeIcon.setImageResource(fileTypeIcons[fileType] ?: R.drawable.ic_add_new_image_icon)
        }

        override fun getItemCount(): Int = mediaList.size

        fun updateList(newList: List<RecentMediaEntity>) {
            mediaList = newList
            filteredList = newList // Ensure the filter list is updated
            notifyDataSetChanged()
        }*/

    /*fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            mediaList
        } else {
            mediaList.filter {
                val fileName = Uri.decode(it.filePath).substringAfterLast("/")
                val match =  fileName.contains(query, ignoreCase = true)
                Log.d("FilterDebug", "Checking file: $fileName, Match: $match")
                match
            }
        }
        Log.d("FilterDebug", "Filtered list size: ${filteredList.size}")
        notifyDataSetChanged()
    }*/

    fun filter(query: String) {
        Log.d("FilterDebug", "Filtering for query: '$query'")

        if (mediaList.isEmpty()) {
            Log.d("FilterDebug", "⚠ No data available in mediaList to filter.")
            return
        }

        filteredList = if (query.isEmpty()) {
            mediaList  //Show full list when query is empty
        } else {
            mediaList.filter {
                val fileName = Uri.decode(it.filePath).substringAfterLast("/")
                val match = fileName.contains(query, ignoreCase = true)
                Log.d("FilterDebug", "Checking file: $fileName, Match: $match")
                match
            }
        }

        Log.d("FilterDebug", "Filtered list size: ${filteredList.size}")
        notifyDataSetChanged()
    }


}