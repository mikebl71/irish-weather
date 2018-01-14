package com.mikebl71.android.irishweather;

import android.app.Application;

/**
 * Main application class.
 */
public class MainApplication extends Application {

    private final DublinModel dublinModel = new DublinModel();
    private final LatestModel latestModel = new LatestModel();
    private final RainRadarModel rainRadarModel = new RainRadarModel();
    private final OutlookModel outlookModel = new OutlookModel();


    public DublinModel getDublinModel() {
        return dublinModel;
    }

    public LatestModel getLatestModel() {
        return latestModel;
    }

    public RainRadarModel getRainRadarModel() {
        return rainRadarModel;
    }

    public OutlookModel getOutlookModel() {
        return outlookModel;
    }


    public void clearModelCaches() {
        dublinModel.clearCache();
        latestModel.clearCache();
        rainRadarModel.clearCache();
        outlookModel.clearCache();
    }

}
