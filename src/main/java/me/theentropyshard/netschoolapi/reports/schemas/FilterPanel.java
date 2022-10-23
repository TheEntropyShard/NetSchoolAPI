package me.theentropyshard.netschoolapi.reports.schemas;

import java.util.Arrays;

public class FilterPanel {
    public Filter[] filters;

    @Override
    public String toString() {
        return "FilterPanel{" +
                "filters=" + Arrays.toString(filters) +
                '}';
    }
}
