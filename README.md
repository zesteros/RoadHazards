# Live Sense SDK Example

Live Sense SDK Example is a example project to show the SDK features of object detection using Camera.

Implements the following Live Sense Models:
  - Road Sign Model
  - Road Basics Model

Application is built for Live Sense SDK v1.0.0+

## Running the Application

Follow the SDK setup steps as described in the root [README](../README.md) file.

## Application Variants
There are 2 application product flavors:
- roadSign
- roadBasic

The flavor determines which model is loaded when launching the application.

To change the product flavor in Android Studio, open the Build Variants window, select the variant, and run the app.
Build Variants are:
- roadSignDebug
- roadBasicDebug
- roadSignRelease
- roadBasicRelease

## Steps to use the SDK in separate app.

**Note:** For more details on Live Sense models and their options, see the developer's guide.

### Models
1.	Setup Live Sense SDK in separate project
2.  Copy the `com.here.livesense_example.modelManager.RecognitionManager` class
3.  Extend `RecognitionManager` and implement model methods, see `RoadBasicsManager` and `RoadSignManager` as manager examples for a single model.
    * `RecognitionManager` handles typical threading and tracking concerns
    * `RecognitionManager` expects YUV Image instances from Camera2 API for tracking and will need some modification if a different source is used


> **Note:** Different trackers are recommended for different models:
>   * Road Basics Model  -   MultiBoxObjectTracker
>   * Road Alerts Model  -   MultiBoxObjectTracker
>   * Road Sign Model    -   BasicObjectTracker
>   * Road Hazard Model  -   BasicObjectTracker


### Camera And Recognition Rendering
1.  Copy the `CameraInstance` class from example project or initialise your own Camera classes
    * See `BaseActivity` for example of `CameraInstance` usage.

2.  Create an Application class and add the following code to onCreate()
    * See `ExampleApplication`.
    ```
       // Get the live sense engine instance
       val liveSenseEngine = LiveSenseEngine.getInstance()
       // Initialise the Live Sense Engine.
       liveSenseEngine.initialize(this)
    ```

3.  Copy layout for activity
    * `activity_main.xml`

4.  Create a new Activity that uses the layout from `activity_main.xml`

5.  Request user consent (**Premium License Only**):
    * Call `liveSenseEngine.requestConsent()` and initialise the model manager in the callback.
    * If consent is not requested, no Live Sense models may be initialized.

    Example:
    ```text
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            // set contentView
            LiveSenseEngine.getInstance().requestConsent(this, object: LiveSenseEngine.LSDConsentCallback {
                        override fun onRefuse() {
                            // Initialize the model manager
                        }

                        override fun onAccept() {
                            // Initialize the model manager
                        }
                    })
        }

    ```

6.  Initialize your model manager and feed images from the camera to the manager instance
    * See `RoadSignsActivity` or `RoadBasicsActivity` for example of manager usage.
    ```text
       private RoadBasicsManager roadBasicsManager;
       private List<TrackedRecognition> trackedObjects;

       // In onCreate()
       roadBasicsManager = RoadBasicsManager.getInstance(this);
       roadBasicsManager.init();

       // In processImage(Image)
       trackedObjects = roadBasicsManager.trackImage(image, getOrientation());
    ```

7. Send the `TrackedRecognition` list returned by your model manager to `RecognitionView` to display bounding boxes over the camera preview.
    * `recognitionView.trackedRecognitions = trackedObjects`
    * See `RoadSignsActivity` or `RoadBasicsActivity` for details.

