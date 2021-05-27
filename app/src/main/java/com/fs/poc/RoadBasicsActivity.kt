package com.fs.poc

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.fs.poc.api.CommonResponse
import com.fs.poc.api.RequestService
import com.fs.poc.data.model.Category
import com.fs.poc.data.model.Ticket
import com.fs.poc.detection.RoadBasicsManager
import com.fs.poc.utils.Singleton
import com.fs.poc.view.BaseActivity
import com.google.gson.Gson
import com.here.see.livesense.ar_lib.camera.LSDRawImageReader
import com.here.see.livesense.tracker.TrackedRecognition
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import java.text.SimpleDateFormat
import java.util.*

/**
 * Activity showing recognitions from RoadBasic model
 */
class RoadBasicsActivity : BaseActivity() {

    // Model manager object
    private lateinit var roadBasicsManager: RoadBasicsManager

    // List of latest tracked recognitions for display
    private var trackedObjects: List<TrackedRecognition> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        // Setting the callback to get the image from camera
        imageAvailableCallback = rbImageAvailableCallback
        // Getting instance of Road Basics Model manager and initialising it
        roadBasicsManager = RoadBasicsManager.getInstance(this)
        roadBasicsManager.init()
    }

    /**
     * Image callback that returns image object from Camera,
     * to be used for recognition input
     */
    private val rbImageAvailableCallback = LSDRawImageReader.ImageAvailableCallback { image ->
        // Feed image to Tracker and Models
        trackedObjects = roadBasicsManager.trackImage(image, getOrientation())
        // trackedObjects contains the detections.
        recognitionView?.trackedRecognitions = trackedObjects

        feedServer(trackedObjects)
    }

    private fun feedServer(trackedObjects: List<TrackedRecognition>) {
        val trackedObjectsIterator = trackedObjects.iterator()
        while (trackedObjectsIterator.hasNext()) {
            val trackedObject = trackedObjectsIterator.next()
            if (trackedObject.title.equals("traffic-light"))
                sendTicket(trackedObject)

        }
    }

    private fun sendTicket(trackedObject: TrackedRecognition) {
        val disposableObserver = object : DisposableObserver<CommonResponse>() {
            override fun onComplete() {}
            override fun onNext(value: CommonResponse?) {
                if (value?.status == 200)
                    Toast.makeText(
                        applicationContext,
                        "Ticket send successfully",
                        Toast.LENGTH_LONG
                    ).show()
            }

            override fun onError(e: Throwable?) {
            }
        }
        val ticket = Ticket()
        ticket.category = Category()
        ticket.category.categoryTypeId = 1
        ticket.category.name = "Robos"
        ticket.category.prefix = "R"
        ticket.category.id = 1

        ticket.categoryId = 1
        ticket.categoryTypeId = 1
        ticket.subCategoryId = 1
        ticket.title = "Ticket de robos Android"
        ticket.deleted = false
        ticket.content = getContent(trackedObject)
        val token = Singleton.getToken()
        Log.i("ticketResult", Gson().toJson(ticket))
        if (token != null) {
            RequestService.getAPI()?.addTicket(token, ticket)
                ?.subscribeOn(AndroidSchedulers.mainThread())
                ?.subscribeWith(disposableObserver)
        }
    }

    private fun getContent(trackedObject: TrackedRecognition): String? {

        val sdf = SimpleDateFormat("MM/dd/YYYY, HH:mm:ss")
        val date = sdf.format(Date())
        val androidVersion = Build.VERSION.SDK_INT
        var content =

            "{batteryLevel=-1;" +
                    " batteryState=0; " +
                    "boundingBox=NSRect:{{${trackedObject.location.bottom},${trackedObject.location.top}},{${trackedObject.location.left},${trackedObject.location.right}}};" +
                    "confidence=${trackedObject.detectionConfidence};" +
                    "confidencethreshold=0.4;" +
                    "coordinate=<+21.08929885,-101.63284261> +/- 65.00m (speed -1.00 mps / course -1.00) @ {$date} Central Daylight Time;" +
                    "deviceAngle=0.4;" +
                    "iouththreshold=0.4;" +
                    "modelName=Roadbasics;" +
                    "object=${trackedObject.title};" +
                    "platform=Android;" +
                    "platformVersion={$androidVersion};" +
                    "uuid=${trackedObject.id};"
        return content
    }
}
