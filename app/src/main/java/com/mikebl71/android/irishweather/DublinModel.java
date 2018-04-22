package com.mikebl71.android.irishweather;

import com.mikebl71.android.common.AbstractContentTransformer;
import com.mikebl71.android.common.ResourceContent;
import com.mikebl71.android.common.UrlManager;

/**
 * Model for the weather forecast for Dublin.
 */
public class DublinModel extends AbstractTextResourceModel {

    private final UrlManager urlManager = UrlManager.builder()
            .url("http://archive.met.ie/forecasts/regional.asp?Prov=Dublin")
            .bodyFrom("<span class=\"orangetext\">")
            .bodyTo("<p><a name=\"l\"")
            .contentTransformer(new DublinTransformer())
            .build();

    @Override
    protected UrlManager getUrlManager() {
        return urlManager;
    }


    private static class DublinTransformer extends AbstractContentTransformer {
        @Override
        protected ResourceContent doTransform(ResourceContent content) {
            String text = content.getString();
            text = text.replaceAll("<span class=\"orangetext\">", "<h1>");
            text = text.replaceAll("<p><span class=\"daybox\">", "<h3>");
            text = text.replaceAll("</b></span>", "</h1>");
            text = text.replaceAll("</span>", "</h3>");
            text = text.replaceAll("<br/><br/>", "<p>");
            text = text.replaceAll("<br/>", "");
            text = text.replaceAll("<b>", "");
            text = text.replaceAll("\t", "");

            return new ResourceContent(text);
        }
    }

}
