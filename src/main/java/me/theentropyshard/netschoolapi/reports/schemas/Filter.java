package me.theentropyshard.netschoolapi.reports.schemas;

import java.util.Arrays;

public class Filter {
    public String id;
    public String title;
    public int order;
    public String filterType;
    public boolean optionalFlag;
    public boolean hideSingleOption;
    public boolean hasSureCheckedFlag;
    public boolean hideTitleFlag;
    public boolean existStateProvider;
    public boolean showAllValueIfSingleFlag;
    public String emptyText;
    public Dependency[] dependencies;

    @Override
    public String toString() {
        return "Filter{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", order=" + order +
                ", filterType='" + filterType + '\'' +
                ", optionalFlag=" + optionalFlag +
                ", hideSingleOption=" + hideSingleOption +
                ", hasSureCheckedFlag=" + hasSureCheckedFlag +
                ", hideTitleFlag=" + hideTitleFlag +
                ", existStateProvider=" + existStateProvider +
                ", showAllValueIfSingleFlag=" + showAllValueIfSingleFlag +
                ", emptyText='" + emptyText + '\'' +
                ", dependencies=" + Arrays.toString(dependencies) +
                '}';
    }
}
