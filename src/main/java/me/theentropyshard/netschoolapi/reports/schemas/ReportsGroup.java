package me.theentropyshard.netschoolapi.reports.schemas;

import java.util.Arrays;

public class ReportsGroup {
    public String id;
    public String notices;
    public Report[] reports;
    public String title;

    public static class Report {
        public String id;
        public String path;
        public String title;

        @Override
        public String toString() {
            return "Report{" +
                    "id='" + id + '\'' +
                    ", path='" + path + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ReportsGroup{" +
                "id='" + id + '\'' +
                ", notices='" + notices + '\'' +
                ", reports=" + Arrays.toString(reports) +
                ", title='" + title + '\'' +
                '}';
    }
}
