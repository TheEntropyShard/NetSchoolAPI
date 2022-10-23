package me.theentropyshard.netschoolapi.reports.schemas;

import java.util.Arrays;

public class FilterSource {
    public Item[] items;
    public String defaultValue;
    public String filterId;
    public Object nullText;
    public String minValue;
    public String maxValue;
    public String message;
    public DateRange range;
    public DateRange defaultRange;

    @Override
    public String toString() {
        return "FilterSource{" +
                "items=" + Arrays.toString(items) +
                ", defaultValue='" + defaultValue + '\'' +
                ", filterId='" + filterId + '\'' +
                ", nullText=" + nullText +
                ", minValue='" + minValue + '\'' +
                ", maxValue='" + maxValue + '\'' +
                ", message='" + message + '\'' +
                ", range=" + range +
                ", defaultRange=" + defaultRange +
                '}';
    }
}
