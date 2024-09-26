package com.hajri.camerasdk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.hajri.cameralibrary.AuthenticationManager
import com.hajri.cameralibrary.PhotoManager

class MainActivity : AppCompatActivity() {

    private lateinit var captButton: Button
    private lateinit var accessButton: Button
    private lateinit var previewView: PreviewView

    private lateinit var photoManager: PhotoManager
    private lateinit var authenticationManager: AuthenticationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        captButton = findViewById(R.id.image_capture_button)
        accessButton = findViewById(R.id.access_button)
        previewView = findViewById(R.id.viewFinder)

        photoManager = PhotoManager(this, previewView, this)
        authenticationManager = AuthenticationManager(this)

        // Request camera permissions
        if (allPermissionsGranted()) {
            photoManager.startCamera()
        } else {
            requestPermission()
        }

        captButton.setOnClickListener { photoManager.takePhoto() }
        accessButton.setOnClickListener {
            authenticationManager.authenticateUser(onSuccess = {
                accessPhotos()
            }, onError = { errorMsg ->
                Toast.makeText(
                    baseContext,
                    errorMsg,
                    Toast.LENGTH_SHORT
                ).show()
            })
        }
    }

    private fun accessPhotos() {
        val photoPaths = photoManager.accessPhotos().map { it.absolutePath }.toTypedArray()
        photoManager.accessPhotos()
        val intent = Intent(this, ActivityGallery::class.java)
        intent.putExtra("photo_paths", photoPaths)
        startActivity(intent)
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(
                    baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                photoManager.startCamera()
            }
        }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}