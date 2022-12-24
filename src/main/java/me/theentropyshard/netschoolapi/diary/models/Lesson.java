/*      NetSchoolAPI. A simple API client for NetSchool by irTech
 *      Copyright (C) 2022 TheEntropyShard
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.theentropyshard.netschoolapi.diary.models;

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
