package com.mikebl71.android.common;

/**
 * Listener for resource content retrieval.
 */
public interface ReadyListener {

    /**
     * Called when the resource content is ready (or failed to be retrieved).
     *
     * @param content  resource content
     */
    void ready(ResourceContent content);

}
