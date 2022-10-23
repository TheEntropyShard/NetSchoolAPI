package me.theentropyshard.netschoolapi.reports.schemas;

public class DateRange {
    public String start;
    public String end;

    @Override
    public String toString() {
        return "DateRange{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                '}';
    }
}
