package com.mikebl71.android.irishweather;

import com.mikebl71.android.common.AbstractContentTransformer;
import com.mikebl71.android.common.ResourceContent;
import com.mikebl71.android.common.UrlManager;

/**
 * Model for the 3-day weather forecast.
 */
public class OutlookModel extends AbstractTextResourceModel {

    private final UrlManager urlManager = UrlManager.builder()
            .url("http://www.met.ie/forecasts/")
            .bodyFrom("<span class=\"daybox\">Outlook</span>")
            .bodyTo("</td>")
            .contentTransformer(new OutlookTransformer())
            .build();

    @Override
    protected UrlManager getUrlManager() {
        return urlManager;
    }


    private static class OutlookTransformer extends AbstractContentTransformer {
        @Override
        protected ResourceContent doTransform(ResourceContent content) {
            String frag = content.getString();
            frag = frag.replaceAll("<span class=\"daybox\">", "<h1>");
            frag = frag.replaceAll("</span><br />", "</h1>");
            frag = frag.replaceAll("<br />\\s*", "<p>");
            frag = frag.replaceAll("<br>", "</p><p>");
            frag = frag.replaceAll("\\s*</td>", "</p>");

            return new ResourceContent(frag);
        }
    }

}
