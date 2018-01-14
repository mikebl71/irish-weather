package com.mikebl71.android.common;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager that retrieves and caches resource content from a URL.
 *
 * Note: It is expected that a Manager can have only one active call-back at a time.
 */
public class UrlManager {

    private static final long DEFAULT_UPTODATE_MS = 5 * 60 * 1000;        // 5 min

    private final String url;
    private final Map<String,String> headers;
    private final String bodyFrom;
    private final String bodyTo;
    private final ContentTransformer contentTransformer;
    private final Object reference;
    private final long upToDateMs;

    private ResourceContent cachedValue;
    private long cacheTimestamp;

    private ReadyListener readyCallback;

    private AsyncTask<Void, Void, ResourceContent> task;

    public static Builder builder() {
        return new Builder();
    }

    private UrlManager(String url, Map<String,String> headers, String bodyFrom, String bodyTo,
                       ContentTransformer contentTransformer, Object reference, long upToDateMs) {
        this.url = url;
        this.headers = headers;
        this.bodyFrom = bodyFrom;
        this.bodyTo = bodyTo;
        this.contentTransformer = contentTransformer;
        this.reference = reference;
        this.upToDateMs = upToDateMs;
    }

    public <T> T getReference(Class<T> resultClass) {
        return (T) reference;
    }


    public void provideResourceContent(final ReadyListener readyCallback) {
        invalidateCallback();
        this.readyCallback = readyCallback;

        if (cachedValue != null && System.currentTimeMillis() - cacheTimestamp < upToDateMs) {
            Log.d("UrlManager", "re-using");
            onContentReady(cachedValue);

        } else if (task == null) {
            Log.d("UrlManager", "retrieving");
            task = new RetrieveAsyncTask();
            task.execute();
        }
        // else the task is running and will call the new readyCallback when completed
    }

    public void invalidateCallback() {
        readyCallback = null;
    }

    public void clearCache() {
        cachedValue = null;
        cacheTimestamp = 0;
    }

    private void onContentReady(ResourceContent result) {
        if (readyCallback != null) {
            readyCallback.ready(result);
            readyCallback = null;
        }
    }

    /**
     * Builder class.
     */
    public static class Builder {
        private String url;
        private Map<String,String> headers = new HashMap<>();
        private String bodyFrom;
        private String bodyTo;
        private ContentTransformer contentTransformer;
        private Object reference;
        private long upToDateMs = DEFAULT_UPTODATE_MS;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder bodyFrom(String bodyFrom) {
            this.bodyFrom = bodyFrom;
            return this;
        }

        public Builder bodyTo(String bodyTo) {
            this.bodyTo = bodyTo;
            return this;
        }

        public Builder contentTransformer(ContentTransformer contentTransformer) {
            this.contentTransformer = contentTransformer;
            return this;
        }

        public Builder reference(Object reference) {
            this.reference = reference;
            return this;
        }

        public Builder upToDateMs(long upToDateMs) {
            this.upToDateMs = upToDateMs;
            return this;
        }

        public UrlManager build() {
            return new UrlManager(url, headers, bodyFrom, bodyTo, contentTransformer, reference, upToDateMs);
        }
    }


    /**
     * Task that retrieves resource content.
     */
    @SuppressLint("StaticFieldLeak")
    private class RetrieveAsyncTask extends AsyncTask<Void, Void, ResourceContent> {

        @Override
        protected ResourceContent doInBackground(Void... urls) {
            // note: no access to non-synchronised object members from background thread

            ResourceContent result = WebClientFactory.getClient().retrieve(url, headers, bodyFrom, bodyTo);

            if (contentTransformer != null) {
                result = contentTransformer.transform(result);
            }

            result.setReference(reference);

            return result;
        }

        @Override
        protected void onPostExecute(ResourceContent result) {
            task = null;
            if (result.isValid()) {
                cachedValue = result;
                cacheTimestamp = System.currentTimeMillis();
            }
            onContentReady(result);
        }
    }

}
