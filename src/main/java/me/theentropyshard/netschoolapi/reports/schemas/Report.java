package me.theentropyshard.netschoolapi.reports.schemas;

import java.util.Arrays;

public class Report {
    public String id;
    public String name;
    public String group;
    public String level;
    public int order;
    public FilterPanel filterPanel;
    public Object[] presentTypes;

    @Override
    public String toString() {
        return "Report{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", group='" + group + '\'' +
                ", level='" + level + '\'' +
                ", order=" + order +
                ", filterPanel=" + filterPanel +
                ", presentTypes=" + Arrays.toString(presentTypes) +
                '}';
    }
}
