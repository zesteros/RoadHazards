package com.fs.poc.detection;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import com.here.see.common.util.permission.AuthorizationException;
import com.here.see.livesense.domain.Recognition;
import com.here.see.livesense.tracker.BasicObjectTracker;
import com.here.see.livesense.tracker.MultiBoxObjectTracker;
import com.here.see.livesense.tracker.TrackedRecognition;
import com.here.see.livesense.util.ImageUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Base class to manage threading and tracking around one or more models.
 */
public abstract class RecognitionManager {
    private static final String TAG = RecognitionManager.class.getSimpleName();
    protected boolean isInit = false;
    protected final Application application;
    // Minimum confidence for model
    private float minimumConfidence = 0.6f;
    // Thread to execute detections
    private HandlerThread inferenceThread;
    private Handler inferenceHandler;
    // Use full or basic tracking (preference depends on model)
    protected boolean useObjectTracker = true;
    // Tracker instances
    private MultiBoxObjectTracker objectTracker;
    private BasicObjectTracker basicObjectTracker;
    // Frame sequence value for trackers
    private long timestamp = 0;

    // Sizes of last image to check for changes in shape
    private int lastWidth = 0;
    private int lastHeight = 0;
    // Buffers for image data
    private byte[][] yuvBytes = new byte[3][];
    private int[] rgbBytes = null;

    // Parameters for asynchronous recognition
    private boolean isRecognitionPending = false;
    private byte[] recognitionLuminance = null;
    private int recognitionOrientation = 0;
    private long recognitionTimestamp = 0;
    private Bitmap recognitionBitmap = null;

    protected RecognitionManager(Context context) {
        application = (Application) context.getApplicationContext();
    }

    /**
     * Initializes manager.
     * Must be called before {@link #trackImage(Image, int)} will function.
     */
    protected void init() {
        // Ensure previous resources are released
        close();
        // Trackers
        objectTracker = new MultiBoxObjectTracker();
        basicObjectTracker = new BasicObjectTracker();
        // Thread for inference
        inferenceThread = new HandlerThread("inference");
        inferenceThread.start();
        inferenceHandler = new Handler(inferenceThread.getLooper());
        this.inferenceHandler.post(() -> {
            try {
                // Initialize model(s) on same thread it will be called
                initModel();
                isInit = true;
            } catch (IOException | AuthorizationException e) {
                // Unable to load model or missing/invalid credentials
                throw new RuntimeException("Exception during model initialization.", e);
            }
        });
    }

    /**
     * Track current image and run recognition if previous invocation is done.
     *
     * @param image             Image to process. Caller responsible for closing image.
     * @param sensorOrientation Current orientation
     * @return List of tracked objects
     */
    public List<TrackedRecognition> trackImage(Image image, int sensorOrientation) {
        if (!isInit) {
            Log.w(TAG, "Manager not initialized.");
            return Collections.emptyList();
        }
        if(image.getFormat() != ImageFormat.YUV_420_888) {
            Log.w(TAG, "Unsupported image format.");
            return Collections.emptyList();
        }
        int width = image.getWidth();
        int height = image.getHeight();
        boolean sizeChanged = width != lastWidth || height != lastHeight;
        // Buffer YUV values
        final Image.Plane[] planes = image.getPlanes();
        final int yRowStride = planes[0].getRowStride();
        for (int i = 0; i < planes.length; ++i) {
            final ByteBuffer buffer = planes[i].getBuffer();
            if (yuvBytes[i] == null) {
                yuvBytes[i] = new byte[buffer.capacity()];
            }
            buffer.get(yuvBytes[i]);
        }
        byte[] luminance = yuvBytes[0];

        // Increment tracker frame count
        timestamp++;
        // Send frame to tracker
        if (useObjectTracker) {
            objectTracker.onFrame(
                    width,
                    height,
                    yRowStride,
                    sensorOrientation,
                    luminance,
                    timestamp);
        } else {
            basicObjectTracker.onFrame(timestamp);
        }

        if (!isRecognitionPending) {
            // Prepare image for recognition
            this.isRecognitionPending = true;

            this.recognitionOrientation = sensorOrientation;
            this.recognitionTimestamp = timestamp;

            // YUV to RGB
            final int uvRowStride = planes[1].getRowStride();
            final int uvPixelStride = planes[1].getPixelStride();

            if (sizeChanged) {
                rgbBytes = new int[width * height];
            }

            ImageUtils.convertYUV420ToARGB8888(
                    yuvBytes[0],
                    yuvBytes[1],
                    yuvBytes[2],
                    width,
                    height,
                    yRowStride,
                    uvRowStride,
                    uvPixelStride,
                    rgbBytes);

            if (sizeChanged) {
                recognitionBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            }
            recognitionBitmap.setPixels(rgbBytes, 0, width, 0, 0, width, height);

            // Hold on to luminance for tracker
            if (sizeChanged) {
                recognitionLuminance = new byte[luminance.length];
            }
            System.arraycopy(luminance, 0, recognitionLuminance, 0, luminance.length);
            // Start recognition
            queueRecognition();
        }

        lastWidth = width;
        lastHeight = height;

        if (useObjectTracker) {
            return objectTracker.getTrackedObjects();
        } else {
            return basicObjectTracker.getTrackedObjects();
        }
    }

    private void queueRecognition() {
        this.inferenceHandler.post(() -> {
            // Run model(s)
            final List<Recognition> recognitions = recognizeImage(recognitionBitmap, recognitionOrientation, minimumConfidence);
            // Send results to tracker
            trackResults(recognitions, recognitionTimestamp, recognitionLuminance);
            isRecognitionPending = false;
        });
    }

    private void trackResults(List<Recognition> recognitions, long timestamp, byte[] luminance) {
        if (useObjectTracker) {
            objectTracker.trackResults(recognitions, luminance, timestamp);
        } else {
            basicObjectTracker.trackResults(recognitions, timestamp);
        }
    }

    /**
     * Cleanup model and tracker resources
     */
    public void close() {
        isInit = false;
        if (inferenceThread != null) {
            inferenceThread.quitSafely();
            inferenceThread = null;
            inferenceHandler = null;
        }
        if (objectTracker != null) {
            objectTracker.close();
            objectTracker = null;
        }
        if (basicObjectTracker != null) {
            basicObjectTracker = null;
        }
        closeModel();
    }

    public float getMinimumConfidence() {
        return minimumConfidence;
    }

    public void setMinimumConfidence(float minimumConfidence) {
        if (minimumConfidence < 0) {
            minimumConfidence = 0f;
        } else if (minimumConfidence > 1) {
            minimumConfidence = 1f;
        }
        this.minimumConfidence = minimumConfidence;
    }

    /**
     * Initialize Live Sense model(s)
     * @throws IOException Failure to load model
     * @throws AuthorizationException Missing/Invalid credentials
     */
    abstract void initModel() throws IOException, AuthorizationException;

    /**
     * Perform recognition using Live Sense models
     */
    abstract List<Recognition> recognizeImage(Bitmap bitmap, int sensorOrientation, float minimumConfidence);

    /**
     * Release all model resources
     */
    abstract void closeModel();
}
