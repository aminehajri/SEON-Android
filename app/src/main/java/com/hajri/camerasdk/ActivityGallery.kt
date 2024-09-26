package com.hajri.camerasdk

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ActivityGallery : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var photosAdapter: PhotosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gallery)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        val photoPaths = intent.getStringArrayExtra("photo_paths") ?: emptyArray()
        val savedPhotos = photoPaths.map { File(it) }

        photosAdapter = PhotosAdapter(savedPhotos)
        recyclerView.adapter = photosAdapter
    }
}