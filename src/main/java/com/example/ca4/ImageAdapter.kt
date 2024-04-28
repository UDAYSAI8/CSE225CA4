package com.example.ca4

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ImageAdapter(private val images: List<File>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    private val selectedImages = BooleanArray(images.size)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Log.d("ImageAdapter", "onBindViewHolder: $position")
        Log.d("ImageAdapter", "images size: ${images.size}")
        Log.d("ImageAdapter", "selectedImages size: ${selectedImages.size}")
        //complete the code to give no index out of bounds exception
        if (position < images.size) {
            val image = images[position]
            holder.imageView.setImageURI(Uri.fromFile(image))
            Log.d("ImageAdapter", "onBindViewHolder: ${Uri.fromFile(image)}")

        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun getSelectedImages(): List<File> {
        val selectedImagesList = mutableListOf<File>()
        for (i in selectedImages.indices) {
            if (selectedImages[i]) {
                selectedImagesList.add(images[i])
            }
        }
        return selectedImagesList
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }
}
