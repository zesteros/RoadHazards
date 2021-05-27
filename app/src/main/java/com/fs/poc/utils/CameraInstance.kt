package com.fs.poc.utils

import android.content.Context
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.util.Log
import android.view.Surface
import com.here.see.livesense.ar_lib.camera.LSDCameraStream
import com.here.see.livesense.ar_lib.camera.LSDRawImageReader
import com.here.see.livesense.ar_lib.surface.LSDSurfaceManager
import com.here.see.livesense.common_lib.utils.LSDCallbackOne

/**
 * Camera Holder class having abstraction of ar-lib camera feature. It is a single class producing
 * image in callback for using it as input to models.
 */
object CameraInstance {
    val cameraStream = LSDCameraStream()
    private val imageReader = LSDRawImageReader()
    lateinit var surfaceManager: LSDSurfaceManager

    // Processing image size
    private const val width = 640
    private const val height = 480
    private var setup = false
    private var surfaces: MutableList<Surface>? = null

    private var surfaceCallback = LSDCallbackOne<MutableList<Surface>> { surfaces ->
        Log.d("Surface for Image reader and Camera View", surfaces.toString())

        // Wait until all surfaces are available
        // 2 in this example (image reader and CameraView)
        if (surfaces.size == 2) {
            CameraInstance.surfaces = surfaces
            cameraStream.waitCameraOpened(LSDCallbackOne<CameraDevice> { cameraDevice ->
                Log.d("Camera stream success callback", cameraDevice.toString())
                cameraStream.createCameraSession(
                    surfaces,
                    cameraCaptureSession,
                    cameraCaptureSessionError
                )
            })
        } else {
            CameraInstance.surfaces = null
        }
    }

    private var cameraCaptureSession = LSDCallbackOne<CameraCaptureSession> { cameraSession ->
        Log.d("cameraCaptureSession Success", cameraSession.toString())
        cameraStream.setCameraSession(
            surfaceManager.getEnabledSurfaces())
    }

    private var cameraCaptureSessionError = LSDCallbackOne<Throwable> { error ->
        Log.d("cameraCaptureSession Error", error.toString())
        cameraStream.setCameraSession(
            surfaceManager.getEnabledSurfaces()
        )
    }

    // Callback when camera is successfully initialised
    private var cameraSuccessCallback = LSDCallbackOne<CameraDevice> { camera ->
        Log.d("Camera Open", camera.toString())
        surfaces?.let { surfaces ->
            if(surfaces.size == 2) {
                // This is to handle case where camera is opened without change in surface list of surfaceManager
                cameraStream.createCameraSession(
                    surfaces,
                    cameraCaptureSession,
                    cameraCaptureSessionError
                )
            } else {
                Log.d("cameraSuccessCallback", "Surface Change Error")
            }
        }
    }

    // Callback when camera did not initialise, error
    private var cameraErrorCallback = LSDCallbackOne<Throwable> { error ->
        Log.d("Camera Error ", error.toString())
    }

    /**
     * Call this method to setup camera, and use the image object in the callback as an input for
     * models.
     */
    fun setupCamera(
        context: Context
    ) {
        // Return if already setup
        if (setup) {
            return
        }
        setup = true
        // Initialize with back facing camera
        cameraStream.initCamera(context, CameraCharacteristics.LENS_FACING_BACK)
        // Initialize surface manager
        surfaceManager = LSDSurfaceManager()
        // Initialize image reader
        imageReader.initRawImageReader(
            width,
            height
        )

        surfaceManager.registerSurface(
            imageReader.getSurface())
        // Wait for all surfaces and camera open before calling createCameraSession
        surfaceManager.onSurfaceListUpdate =
            surfaceCallback
    }

    /**
     * Open camera and register image available callback
     */
    fun openCamera(context: Context, imageAvailableCallback: LSDRawImageReader.ImageAvailableCallback) {
        // Added a callback for frames as an Image object.
        imageReader.imageListenerCallbacks.add(imageAvailableCallback)
        // Open camera asynchronously
        cameraStream.openCamera(context,
            cameraSuccessCallback,
            cameraErrorCallback
        )
    }

    /**
     * Close camera and deregister image available callback
     */
    fun closeCamera(imageAvailableCallback: LSDRawImageReader.ImageAvailableCallback) {
        // Remove callback
        imageReader.imageListenerCallbacks.remove(imageAvailableCallback)
        // Close camera
        cameraStream.closeCamera()
    }

}