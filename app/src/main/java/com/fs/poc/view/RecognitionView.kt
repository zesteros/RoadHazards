package com.fs.poc.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.here.see.livesense.ar_lib.camera.LSDCameraView
import com.here.see.livesense.tracker.TrackedRecognition
import kotlin.random.Random.Default.nextInt

/**
 * Draws tracked recognitions over an [LSDCameraView]
 */
class RecognitionView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    View(context, attrs, defStyle) {

    var trackedRecognitions: List<TrackedRecognition> = listOf()
        set(value) {
            field = value
            postInvalidate()
        }

    // Matrix to map recognitions from inference to view space
    private var drawMatrix: Matrix? = null

    // Pre-allocated objects for drawing
    private val destRectangle = RectF()
    private val paintBoundingBox =
        createPaint(Color.WHITE)
    private val paintText =
        createTextPaintWhite()
    private val paintTextBackground =
        createTextPaintOutline(Color.WHITE)

    /**
     * Initialize RecognitionView and associate with passed [LSDCameraView]
     */
    fun init(cameraView: LSDCameraView) {
        drawMatrix = cameraView.drawMatrix
        cameraView.onDrawMatrixChanged.addListener {
            drawMatrix = it
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        val canvas = canvas ?: return
        val drawMatrix = this.drawMatrix ?: return
        val lastRecognitions = trackedRecognitions
        for (recognition in lastRecognitions) {
            val rect = recognition.trackedLocation
            // Map bounding box to view space
            drawMatrix.mapRect(destRectangle, rect)
            // Use distinct colors for different recognitions
            paintBoundingBox.color =
                getRecognitionColor(
                    recognition
                )
            paintTextBackground.color = paintBoundingBox.color
            // Draw bounding box
            canvas.drawRect(destRectangle, paintBoundingBox)
            // Add label + confidence
            val text = "${recognition.title} ${recognition.detectionConfidence}"
            // Bordered Text effect
            canvas.drawText(
                text, destRectangle.left, destRectangle.bottom + 20f, paintTextBackground
            )
            canvas.drawText(
                text, destRectangle.left, destRectangle.bottom + 20f, paintText
            )
        }
    }

    private companion object {
        private const val colorKey = "drawColor"
        fun getRecognitionColor(recognition: TrackedRecognition): Int {
            val color = recognition.metaData[colorKey] ?: kotlin.run {
                val c =
                    Color.argb(255, nextInt(256), nextInt(256), nextInt(256))
                // Store color to reuse on next draw
                recognition.metaData[colorKey] = c
                c
            }
            return color as Int
        }

        fun createPaint(color: Int): Paint {
            val paint = Paint()
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 12.0f
            paint.strokeCap = Paint.Cap.ROUND
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeMiter = 100f
            paint.color = color
            return paint
        }

        fun createTextPaintWhite(): Paint {
            val paint = Paint()
            paint.strokeWidth = 1.0f
            paint.textSize = 28f
            paint.color = Color.WHITE
            return paint
        }

        fun createTextPaintOutline(color: Int): Paint {
            val paint = Paint()
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 8.0f
            paint.textSize = 28f
            paint.color = color
            return paint
        }
    }
}