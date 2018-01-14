package com.mikebl71.android.common;

import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Content of a resource.
 * Can be a string, a binary (eg a drawable), or a retrieval exception.
 */
public class ResourceContent implements Serializable {

    private static final long serialVersionUID = -8055180132887625440L;

    private String stringContent;
    private byte[] binaryContent;

    private Exception exception;

    private Object reference;

    private transient Drawable drawableContent;


    public ResourceContent() {
    }

    public ResourceContent(String stringContent) {
        this.stringContent = stringContent;
    }

    public ResourceContent(byte[] binaryContent) {
        this.binaryContent = binaryContent;
    }

    public ResourceContent(Exception ex) {
        exception = ex;
    }


    public boolean isValid() {
        return (exception == null);
    }

    public Exception getException() {
        return exception;
    }


    public String getString() {
        return stringContent;
    }

    public String getStringOrThrow() throws Exception {
        if (exception != null) {
            throw exception;
        }
        return stringContent;
    }

    public void setString(String stringContent) {
        this.stringContent = stringContent;
    }


    public InputStream getBinaryOrThrow() throws Exception {
        if (exception != null) {
            throw exception;
        }
        return new ByteArrayInputStream(binaryContent);
    }


    public Drawable getDrawableOrThrow() throws Exception {
        if (drawableContent == null) {
            try (InputStream is = getBinaryOrThrow()) {
                drawableContent = Drawable.createFromStream(is, "image");
            }
        }
        return drawableContent;
    }


    public <T> T getReference(Class<T> resultClass) {
        return (T) reference;
    }

    public void setReference(Object reference) {
        this.reference = reference;
    }
}
