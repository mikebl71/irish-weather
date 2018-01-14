package com.mikebl71.android.irishweather;

/**
 * Fragment for the latest weather in Dublin.
 */
public class LatestFragment extends AbstractTextResourceFragment {

    @Override
    protected AbstractTextResourceModel lookupModel(MainApplication application) {
        return application.getLatestModel();
    }
}
