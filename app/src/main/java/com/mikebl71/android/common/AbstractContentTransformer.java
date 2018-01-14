package com.mikebl71.android.common;

/**
 * Abstract base class for Content Transformers.
 */
public abstract class AbstractContentTransformer implements ContentTransformer {

    public ResourceContent transform(ResourceContent content) {
        if (!content.isValid()) {
            return content;
        }

        try {
            return doTransform(content);

        } catch (Exception ex) {
            return new ResourceContent(ex);
        }
    }

    protected abstract ResourceContent doTransform(ResourceContent content) throws Exception;
}
