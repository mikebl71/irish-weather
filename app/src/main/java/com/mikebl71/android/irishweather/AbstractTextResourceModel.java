package com.mikebl71.android.irishweather;

import com.mikebl71.android.common.ReadyListener;
import com.mikebl71.android.common.UrlManager;

/**
 * Base Model for a text resource provided by a UrlManager.
 *
 * Note: It is expected that a Model can have only one active call-back at a time.
 */
public abstract class AbstractTextResourceModel {

    public void provideText(ReadyListener readyCallback) {
        getUrlManager().provideResourceContent(readyCallback);
    }

    public void invalidateCallback() {
        getUrlManager().invalidateCallback();
    }

    public void clearCache() {
        getUrlManager().clearCache();
    }


    protected abstract UrlManager getUrlManager();
}
