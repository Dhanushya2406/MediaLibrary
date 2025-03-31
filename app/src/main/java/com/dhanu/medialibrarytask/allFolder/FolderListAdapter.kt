package com.dhanu.medialibrarytask.allFolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dhanu.medialibrarytask.R

class FolderListAdapter(private val folderList: List<FolderItem> ): RecyclerView.Adapter<FolderListAdapter.FolderViewHolder>() {
    class FolderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val folderName: TextView = itemView.findViewById(R.id.folderName)
        val fileCount: TextView = itemView.findViewById(R.id.fileCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.folder_card_list_item,parent,false)
        return FolderViewHolder(view)

    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folderList[position]
        holder.folderName.text = folder.folderName
        holder.fileCount.text = "${folder.fileCount} files"
    }

    override fun getItemCount(): Int {
        return folderList.size
    }

}