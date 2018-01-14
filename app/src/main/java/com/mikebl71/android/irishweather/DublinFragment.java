package com.mikebl71.android.irishweather;

/**
 * Fragment for the weather forecast for Dublin.
 */
public class DublinFragment extends AbstractTextResourceFragment {

    @Override
    protected AbstractTextResourceModel lookupModel(MainApplication application) {
        return application.getDublinModel();
    }
}
