package com.example.ca4

import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ViewImagesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter
    private val imagesList = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_images)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ImageAdapter(imagesList)
        recyclerView.adapter = adapter

        val uploadButton = findViewById<Button>(R.id.uploadButton)
        uploadButton.setOnClickListener {
            openGalleryForUpload()
        }

        val copyButton = findViewById<Button>(R.id.copyButton)
        copyButton.setOnClickListener {
            copyImagesToExternalStorage()
        }

        loadImagesFromInternalStorage()
    }

    private fun loadImagesFromInternalStorage() {
        val filesDir = filesDir
        val files = filesDir.listFiles { _, name -> name.endsWith(".jpg") }
        imagesList.clear()
        imagesList.addAll(files ?: emptyArray())
        adapter.notifyDataSetChanged()
    }

    private fun openGalleryForUpload() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_SELECT_IMAGE)
    }

    private fun saveImageToInternalStorage(imageUri: Uri) {
        val inputStream = contentResolver.openInputStream(imageUri)
        val outputFile = File(filesDir, "image_${System.currentTimeMillis()}.jpg")
        inputStream?.use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun copyImagesToExternalStorage() {
        for (imageFile in imagesList) {
            // Get the content URI of the image file
            val imageUri = FileProvider.getUriForFile(this, "com.example.ca4.fileprovider", imageFile)

            // Define the destination directory in external storage (Downloads directory)
            val externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val destFile = File(externalDir, imageFile.name)

            try {
                // Copy the image file to the external storage
                contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    destFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                // Notify the media scanner to scan the copied image file
                MediaScannerConnection.scanFile(this, arrayOf(destFile.absolutePath), null, null)

                // Show a toast message indicating successful copy
                Toast.makeText(this, "Copied ${imageFile.name} to Downloads", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // Show a toast message if an error occurs during copy
                Toast.makeText(this, "Failed to copy ${imageFile.name} to Downloads", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data
            if (selectedImageUri != null) {
                saveImageToInternalStorage(selectedImageUri)
                loadImagesFromInternalStorage()
            }
        }
    }

    companion object {
        private const val REQUEST_SELECT_IMAGE = 101
    }
}
