package me.theentropyshard.netschoolapi.schemas;

import java.util.Arrays;

public class Lesson {
    public int classmeetingId;
    public String day;
    public int number;
    public int relay;
    public String room;
    public String startTime;
    public String endTime;
    public String subjectName;
    public Assignment[] assignments;
    public boolean isEaLesson;

    @Override
    public String toString() {
        return "Lesson{" +
                "classmeetingId=" + classmeetingId +
                ", day='" + day + '\'' +
                ", number=" + number +
                ", relay=" + relay +
                ", room='" + room + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", assignments=" + Arrays.toString(assignments) +
                ", isEaLesson=" + isEaLesson +
                '}';
    }
}
