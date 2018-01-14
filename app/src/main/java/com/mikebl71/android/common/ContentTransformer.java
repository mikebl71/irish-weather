package com.mikebl71.android.common;

/**
 * Transformer for resource content.
 */
public interface ContentTransformer {

    /**
     * Transforms resource content.
     */
    ResourceContent transform(ResourceContent content);
}
