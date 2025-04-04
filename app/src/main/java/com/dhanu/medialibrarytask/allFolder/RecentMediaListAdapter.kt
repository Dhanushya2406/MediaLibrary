package com.dhanu.medialibrarytask.allFolder

import android.net.Uri
import android.util.Log
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
    }

    override fun getItemCount(): Int = filteredList.size

    fun updateList(newList: List<RecentMediaEntity>) {
        mediaList = newList.toList()  // Store a copy of the full list
        filteredList = newList.toList()  // Reset filteredList
        Log.d("FilterDebug", "Media list updated, size: ${mediaList.size}")
        notifyDataSetChanged()
    }

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