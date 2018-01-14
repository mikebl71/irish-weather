package com.mikebl71.android.common;

import java.util.Comparator;
import java.util.Date;

/**
 * Comparator that sorts dates in descending order.
 */
public class DescendingDateComparator implements Comparator<Date> {

    public int compare(Date o1, Date o2) {
        return o1.compareTo(o2) * (-1);
    }
}
