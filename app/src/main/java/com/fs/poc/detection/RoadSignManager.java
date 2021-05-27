package com.fs.poc.detection;

import android.content.Context;
import android.graphics.Bitmap;
import com.here.see.common.util.permission.AuthorizationException;
import com.here.see.livesense.RoadSignsModel;
import com.here.see.livesense.domain.Recognition;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Manager for instance of RoadSignsModel
 */
public class RoadSignManager extends RecognitionManager {
    private static RoadSignManager ourInstance;
    private RoadSignsModel roadSignsModel;
    private String desiredRegion = "glo";

    private RoadSignManager(Context context) {
        super(context);
    }

    public synchronized static RoadSignManager getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new RoadSignManager(context);
        }
        return ourInstance;
    }

    /**
     * Initializes RoadSigns model with specified region, if region is not available sdk will default to a global setting.
     */
    public void init(String countryCode) {
        if (!isInit || !Objects.equals(countryCode, desiredRegion)) {
            // Use basic tracker for signs
            useObjectTracker = false;
            desiredRegion = countryCode;
            super.init();
        }
    }

    @Override
    void initModel() throws IOException, AuthorizationException {
        // Initialize signs model to selected region
        RoadSignsModel.Options options = new RoadSignsModel.Options();
        options.setNumThreads(4); // 2-4 normally depending upon device/application
        options.setCountryCode(desiredRegion);
        this.roadSignsModel = new RoadSignsModel(application, options);
    }

    @Override
    List<Recognition> recognizeImage(Bitmap bitmap, int sensorOrientation, float minimumConfidence) {
        return roadSignsModel.recognizeImage(bitmap, sensorOrientation, minimumConfidence);
    }

    @Override
    void closeModel() {
        if (this.roadSignsModel != null) {
            this.roadSignsModel.close();
            this.roadSignsModel = null;
        }
    }

    public void useSplitInference(Boolean splitInference) {
        if (roadSignsModel != null) {
            RoadSignsModel.Options rsOptions = roadSignsModel.getOptions();
            rsOptions.setUseSplitInference(splitInference);
            roadSignsModel.setOptions(rsOptions);
        }
    }
}
