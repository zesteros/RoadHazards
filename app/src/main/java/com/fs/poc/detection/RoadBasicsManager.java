package com.fs.poc.detection;

import android.content.Context;
import android.graphics.Bitmap;
import com.here.see.common.util.permission.AuthorizationException;
import com.here.see.livesense.RoadBasicsModel;
import com.here.see.livesense.domain.Recognition;

import java.io.IOException;
import java.util.List;

/**
 * Manager for instance of RoadBasicsModel
 */
public class RoadBasicsManager extends RecognitionManager {
    private static RoadBasicsManager ourInstance;
    private RoadBasicsModel roadBasicsModel;

    private RoadBasicsManager(Context context) {
        super(context);
    }

    public synchronized static RoadBasicsManager getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new RoadBasicsManager(context);
        }
        return ourInstance;
    }

    public void init() {
        if (!isInit) {
            useObjectTracker = true;
            super.init();
        }
    }

    @Override
    void initModel() throws IOException, AuthorizationException {
        RoadBasicsModel.Options roadBasicsOptions = new RoadBasicsModel.Options();
        // To use GPU (Requires SNPE library aar is provided)
//        roadBasicsOptions.setNnEngine(NNEngine.SNPE);
//        roadBasicsOptions.setRuntime(EngineRuntime.GPU);
        roadBasicsOptions.setNumThreads(4); // 2-4 normally depending upon device/application
        this.roadBasicsModel = new RoadBasicsModel(application, roadBasicsOptions);
    }

    @Override
    List<Recognition> recognizeImage(Bitmap bitmap, int sensorOrientation, float minimumConfidence) {
        return this.roadBasicsModel.recognizeImage(bitmap, sensorOrientation, minimumConfidence);
    }

    @Override
    void closeModel() {
        if (this.roadBasicsModel != null) {
            this.roadBasicsModel.close();
            this.roadBasicsModel = null;
        }
    }
}
