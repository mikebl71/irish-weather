package com.mikebl71.android.irishweather;

import java.util.Date;

public class RainRadarLink {

    private final Date date;
    private final String url;

    public RainRadarLink(Date date, String url) {
        this.date = date;
        this.url = url;
    }

    public Date getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }
}
