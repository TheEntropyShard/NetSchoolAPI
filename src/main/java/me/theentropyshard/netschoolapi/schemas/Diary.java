package me.theentropyshard.netschoolapi.schemas;

import java.util.Arrays;

public class Diary {
    public String weekStart;
    public String weekEnd;
    public Day[] weekDays;
    public LaAssign[] laAssigns;
    public String termName;
    public String className;

    @Override
    public String toString() {
        return "Diary{" +
                "weekStart='" + weekStart + '\'' +
                ", weekEnd='" + weekEnd + '\'' +
                ", weekDays=" + Arrays.toString(weekDays) +
                ", laAssigns=" + Arrays.toString(laAssigns) +
                ", termName='" + termName + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}
