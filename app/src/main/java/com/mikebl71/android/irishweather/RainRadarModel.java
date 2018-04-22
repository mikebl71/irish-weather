package com.mikebl71.android.irishweather;

import com.mikebl71.android.common.DescendingDateComparator;
import com.mikebl71.android.common.MultiReadyListener;
import com.mikebl71.android.common.ReadyListener;
import com.mikebl71.android.common.ResourceContent;
import com.mikebl71.android.common.UrlManager;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Model for the rain radar images.
 *
 * Note: It is expected that a Model can have only one active call-back at a time.
 */
public class RainRadarModel {

    private static final int MAX_IMAGES = 4;

    // radar2.xml contains list of links to radar images
    private final UrlManager linksPageUrlManager = UrlManager.builder()
            .url("http://archive.met.ie/weathermaps/radar2/radar4_6hr.xml")
            .header("Referer", "http://archive.met.ie/latest/rainfall_radar.asp")
            .build();

    private SortedMap<Date, UrlManager> imageUrlManagers = new TreeMap<>(new DescendingDateComparator());


    public void provideLatestImage(final ReadyListener readyCallback) {
        invalidateCallback();
        linksPageUrlManager.provideResourceContent(new LinksPageReadyListener(readyCallback));
    }

    public void provideAllImages(final MultiReadyListener readyCallback) {
        invalidateCallback();
        ImageAggregator imageAggregator = new ImageAggregator(readyCallback);
        for (UrlManager urlManager : imageUrlManagers.values()) {
            urlManager.provideResourceContent(imageAggregator);
        }
    }

    public void invalidateCallback() {
        linksPageUrlManager.invalidateCallback();
        for (UrlManager imageUrlManager : imageUrlManagers.values()) {
            imageUrlManager.invalidateCallback();
        }
    }

    public void clearCache() {
        linksPageUrlManager.clearCache();
        // image resources of imageUrlManagers never change so no need to clear them
    }


    /**
     * Parses xml page with links to radar images.
     */
    private class LinksPageReadyListener implements ReadyListener {
        private final ReadyListener latestImageReadyCallback;

        public LinksPageReadyListener(ReadyListener latestImageReadyCallback) {
            this.latestImageReadyCallback = latestImageReadyCallback;
        }

        public void ready(ResourceContent content) {
            try {
                String page = content.getStringOrThrow();   // will throw if failed to retrieve the page
                SortedMap<Date, String> links = RainRadarHelper.parseLinks(page, MAX_IMAGES);   // will throw if not enough links

                Date latestLinkDate = links.firstKey();

                if (!imageUrlManagers.isEmpty() && imageUrlManagers.firstKey().equals(latestLinkDate)) {
                    // this is the same list of links as we've parsed before

                } else {
                    // build the new list of image url managers (but re-use old managers if exist)
                    SortedMap<Date, UrlManager> oldImageUrlManagers = imageUrlManagers;
                    imageUrlManagers = new TreeMap<>(new DescendingDateComparator());

                    for (Map.Entry<Date, String> linkEntry : links.entrySet()) {
                        Date linkDate = linkEntry.getKey();
                        UrlManager urlManager = oldImageUrlManagers.get(linkDate);
                        if (urlManager == null) {
                            urlManager = UrlManager.builder()
                                    .url(linkEntry.getValue())
                                    .header("Referer", "http://archive.met.ie/latest/rainfall_radar.asp")
                                    .reference(linkDate)
                                    .upToDateMs(Long.MAX_VALUE)
                                    .build();
                        }
                        imageUrlManagers.put(linkDate, urlManager);
                    }
                }
                imageUrlManagers.get(latestLinkDate).provideResourceContent(latestImageReadyCallback);

            } catch (Exception ex) {
                latestImageReadyCallback.ready(new ResourceContent(ex));
            }
        }
    }

    /**
     * Aggregates retrieved images.
     */
    private class ImageAggregator implements ReadyListener {
        private final MultiReadyListener imagesReadyCallback;
        private SortedMap<Date, ResourceContent> results = new TreeMap<>(new DescendingDateComparator());

        public ImageAggregator(MultiReadyListener imagesReadyCallback) {
            this.imagesReadyCallback = imagesReadyCallback;
        }

        public void ready(ResourceContent result) {
            Date resultDate = result.getReference(Date.class);
            results.put(resultDate, result);

            if (results.size() == imageUrlManagers.size()) {
                ResourceContent invalidResult = null;
                for (ResourceContent item : results.values()) {
                    if (!item.isValid()) {
                        invalidResult = item;
                        break;
                    }
                }
                if (invalidResult != null) {
                    imagesReadyCallback.ready(Collections.singleton(invalidResult));
                } else {
                    imagesReadyCallback.ready(results.values());
                }
            }
        }
    }

}
