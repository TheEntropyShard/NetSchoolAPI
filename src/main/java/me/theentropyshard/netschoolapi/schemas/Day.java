package me.theentropyshard.netschoolapi.schemas;

import java.util.Arrays;

public class Day {
    public String date;
    public Lesson[] lessons;

    @Override
    public String toString() {
        return "Day{" +
                "date='" + date + '\'' +
                ", lessons=" + Arrays.toString(lessons) +
                '}';
    }
}
