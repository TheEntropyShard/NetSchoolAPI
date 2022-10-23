package me.theentropyshard.netschoolapi.reports.schemas;

import java.util.Arrays;

public class StudentGrades {
    public Report report;
    public FilterSource[] filterSources;

    @Override
    public String toString() {
        return "StudentGrades{" +
                "report=" + report +
                ", filterSources=" + Arrays.toString(filterSources) +
                '}';
    }
}
