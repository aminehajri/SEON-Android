package com.hajri.cameralibrary

import android.app.Activity
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class PhotoManager(
    private val activity: Activity,
    private val previewView: PreviewView,
    private val lifecycleOwner: LifecycleOwner
) {
    private var imageCapture: ImageCapture? = null

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select front camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use case before rebinding
                cameraProvider.unbindAll()

                // Bind use case to camera
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(activity))
    }

    fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Create the image file name
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())

        // Create the subdirectory under Pictures directory
        val photosDir =
            File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), subdirectoryName)
        if (!photosDir.exists()) {
            photosDir.mkdirs()
        }

        val file = File(photosDir, "$name.jpg")

        // Create output options object with file reference
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        // Set up image capture listener, which is triggered after the photo has been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(activity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    val msg = "Photo capture failed"
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded"
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    fun accessPhotos(): List<File> {
        // Access the directory under Pictures
        val photosDir =
            File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), subdirectoryName)
        val files = photosDir.listFiles { file -> file.extension == "jpg" }?.toList() ?: emptyList()
        Log.d(TAG, "accessPhotos: ${files.size}")
        return files
    }

    companion object {
        private const val TAG = "CameraApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val subdirectoryName = "SEON"
    }

}