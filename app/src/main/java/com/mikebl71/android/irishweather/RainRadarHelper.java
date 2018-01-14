package com.mikebl71.android.irishweather;

import android.util.Xml;

import com.mikebl71.android.common.DescendingDateComparator;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Helper methods for Rain Radar processing.
 */
public class RainRadarHelper {

    /**
     * Extracts links from the reference page and sorts them by time descending.
     *
     * The xml page looks like:
     * <radar>
     *   <image day="06" hour="17" min="45" src="WEB_radar5_201801061745.png" />
     *   ..
     *   <image day="06" hour="17" min="00" src="WEB_radar5_201801061700.png" />
     *   <image day="06" hour="17" min="15" src="WEB_radar5_201801061715.png" />
     *   <image day="06" hour="17" min="30" src="WEB_radar5_201801061730.png" />
     *   <image day="06" hour="17" min="45" src="WEB_radar5_201801061745.png" />
     * </radar>
     */
    public static SortedMap<Date, String> parseLinks(String page, int listSize) throws Exception {
        SortedMap<Date, String> sortedLinks = new TreeMap<>(new DescendingDateComparator());

        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new StringReader(page));

        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.US);

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("image")) {
                String fileName = parser.getAttributeValue(null, "src");
                String timestampStr = fileName.substring(fileName.indexOf("20"), fileName.indexOf('.'));
                sortedLinks.put(timestampFormat.parse(timestampStr),
                        "http://www.met.ie/weathermaps/radar2/" + fileName);
            }
            eventType = parser.next();
        }

        if (sortedLinks.size() < listSize) {
            throw new Exception("Not enough radar images: expected " + listSize + ", found " + sortedLinks.size());
        }

        SortedMap<Date, String> sortedCappedLinks = new TreeMap<>(new DescendingDateComparator());
        Iterator<Map.Entry<Date, String>> linkIterator = sortedLinks.entrySet().iterator();
        for (int cnt = 0; cnt < listSize; cnt++) {
            Map.Entry<Date, String> entry = linkIterator.next();
            sortedCappedLinks.put(entry.getKey(), entry.getValue());
        }
        return sortedCappedLinks;
    }

}
