package com.fs.poc.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.Surface
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.here.poc.R
import com.fs.poc.utils.CameraInstance
import com.here.see.livesense.ar_lib.camera.LSDCameraView
import com.here.see.livesense.ar_lib.camera.LSDRawImageReader

/**
 * Base Activity class for displaying camera preview with recognition overlay
 */
open class BaseActivity : AppCompatActivity() {

    // Relative orientation between camera sensor and device, used for rotating image from camera
    private var orientation = 0
    // Camera sensor orientation relative to device's natural orientation
    private var cameraSensorOrientation : Int = 90
    // Overlay for drawing recognitions
    protected var recognitionView: RecognitionView? = null
    // Image callback, assigned by sub-class
    protected var imageAvailableCallback : LSDRawImageReader.ImageAvailableCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main)
        initCamera()
        updateOrientation()
    }

    override fun onResume() {
        super.onResume()
        // Open camera to begin receiving frames
        CameraInstance.openCamera(this, imageAvailableCallback!!)
    }

    override fun onPause() {
        super.onPause()
        // Release camera and unregister callback so we don't leak Activity instance
        CameraInstance.closeCamera(imageAvailableCallback!!)
    }

    private fun initCamera() {
        CameraInstance.setupCamera(this)
        // Camera preview
        val cameraView = findViewById<LSDCameraView>(R.id.cameraView)
        cameraView.deviceRotation = getScreenOrientation()
        cameraView.initCameraView(
            CameraInstance.cameraStream,
            CameraInstance.surfaceManager,
            null,
            true
        )
        cameraSensorOrientation = CameraInstance.cameraStream.cameraProperties?.sensorOrientation ?: 90
        // Initialize recognition view
        val recognitionView = findViewById<RecognitionView>(R.id.recognition_view)
        recognitionView.init(cameraView)
        // Hold onto recognition view instance for drawing
        this.recognitionView = recognitionView
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateOrientation()
    }

    private fun updateOrientation() {
        // Assumes back camera
        orientation = (cameraSensorOrientation - getScreenOrientation() + 360) % 360
    }

    fun getScreenOrientation(): Int {
        return when (windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_270 -> 270
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_0 -> 0
            else -> 0
        }
    }

    fun getOrientation(): Int {
        return orientation
    }
}