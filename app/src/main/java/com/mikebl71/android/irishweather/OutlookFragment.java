package com.mikebl71.android.irishweather;

/**
 * Fragment for the 3-day weather forecast.
 */
public class OutlookFragment extends AbstractTextResourceFragment {

    @Override
    protected AbstractTextResourceModel lookupModel(MainApplication application) {
        return application.getOutlookModel();
    }
}
