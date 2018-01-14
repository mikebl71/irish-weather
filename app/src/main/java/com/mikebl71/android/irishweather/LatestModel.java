package com.mikebl71.android.irishweather;

import com.mikebl71.android.common.AbstractContentTransformer;
import com.mikebl71.android.common.ResourceContent;
import com.mikebl71.android.common.UrlManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Model for the latest weather in Dublin.
 */
public class LatestModel extends AbstractTextResourceModel {

    private final UrlManager urlManager = UrlManager.builder()
            .url("http://www.met.ie/latest/reports.asp")
            .bodyFrom("<b>LATEST IRISH WEATHER REPORTS")
            .bodyTo("</table>")
            .contentTransformer(new LatestTransformer())
            .build();

    @Override
    protected UrlManager getUrlManager() {
        return urlManager;
    }


    private static class LatestTransformer extends AbstractContentTransformer {
        private static final Pattern ROW_PATTERN = Pattern.compile(
                "<tr><td.*?>DUBLIN AIRPORT\\s*" +
                        "</td><td.*?>(.*?)\\s*" +
                        "</td><td.*?>(.*?)\\s*" +
                        "</td><td.*?>(.*?)\\s*" +
                        "</td><td.*?>(.*?)\\s*" +
                        "</td><td.*?>(.*?)\\s*" +
                        "</td><td.*?>(.*?)\\s*" +
                        "</td><td.*?>(.*?)\\s*" +
                        "</b></span></td></tr>");
        @Override
        protected ResourceContent doTransform(ResourceContent content) {
            String table = content.getString();

            table = table.replace("&nbsp;", " ");

            String date = table.substring(
                    table.indexOf("ON") + 2,
                    table.indexOf("</b>"))
                    .trim();

            StringBuilder sb = new StringBuilder(
                    "<h1>Latest Weather Reports</h1>");
            sb.append("Dublin Airport<br/>").append(date).append("<br/><br/>");

            Matcher m = ROW_PATTERN.matcher(table);
            if (m.find()) {
                sb.append("<b>Temp:\t\t</b>").append(m.group(4).trim()).append(" \u00B0C<br/>");
                sb.append("<b>Weather:\t</b>").append(m.group(3).trim()).append("<br/>");
                sb.append("<b>Rain:\t\t\t</b>").append(m.group(6).trim()).append(" mm<br/>");
                sb.append("<b>Wind:\t\t\t</b>").append(m.group(2).trim())
                        .append(" kts (").append(m.group(1).trim()).append(")").append("<br/>");
                sb.append("<b>Humidity:\t</b>").append(m.group(5).trim()).append(" %<br/>");
                sb.append("<b>Pressure:\t</b>").append(m.group(7).trim()).append(" hPa<br/>");
            }

            return new ResourceContent(sb.toString());
        }
    }

}
