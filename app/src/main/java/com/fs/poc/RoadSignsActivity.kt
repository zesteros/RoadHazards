package com.fs.poc

import android.os.Bundle
import com.fs.poc.detection.RoadSignManager
import com.fs.poc.view.BaseActivity
import com.here.see.livesense.ar_lib.camera.LSDRawImageReader
import com.here.see.livesense.tracker.TrackedRecognition

/**
 * Activity showing recognitions from RoadSign model
 */
class RoadSignsActivity : BaseActivity() {

    // Model manager object
    private lateinit var roadSignManager: RoadSignManager

    // List of latest tracked recognitions for display
    private var trackedObjects: List<TrackedRecognition> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        // Setting the callback to get the image from camera
        imageAvailableCallback = rsImageAvailableCallback
        // Getting instance of Road Basics Model manager and initialising it
        roadSignManager = RoadSignManager.getInstance(this)
        roadSignManager.init(null)
    }

    /**
     * Image callback that returns image object from Camera,
     * to be used for recognition input
     */
    private val rsImageAvailableCallback = LSDRawImageReader.ImageAvailableCallback { image ->
        // Feed image to Tracker and Models
        trackedObjects = roadSignManager.trackImage(image, getOrientation())
        // trackedObjects contains the detections.
        recognitionView?.trackedRecognitions = trackedObjects
    }
}
