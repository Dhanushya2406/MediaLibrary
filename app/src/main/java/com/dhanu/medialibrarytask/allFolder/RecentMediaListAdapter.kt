package com.dhanu.medialibrarytask.allFolder

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

    class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileTypeIcon: ImageView = itemView.findViewById(R.id.fileTypeIcon)
        val fileName: TextView = itemView.findViewById(R.id.fileName)
    }

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
        val fileName = if (fileNameWithExt.contains(".")){
            fileNameWithExt.substringBeforeLast(".")  // Remove extension if present
        } else {
            fileNameWithExt  // Keep name as is if there's no extension
        }
        holder.fileName.text = fileName
        holder.fileTypeIcon.setImageResource(fileTypeIcons[media.fileType] ?: R.drawable.ic_add_new_image_icon)
    }

    override fun getItemCount(): Int = mediaList.size

}