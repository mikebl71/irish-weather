package com.mikebl71.android.common;

import java.util.Collection;

/**
 * Listener for resource content retrieval for multiple resources.
 */
public interface MultiReadyListener {

    /**
     * Called when resource contents are ready (or failed to be retrieved).
     *
     * @param contents  collection of resource contents
     */
    void ready(Collection<ResourceContent> contents);

}
