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

package me.theentropyshard.netschoolapi.diary.schemas;

import java.util.Arrays;

public class DetailedAssignment {
    public String activityName;
    public String assignmentName;
    public Attachment[] attachments;
    public String date;
    public String description;
    public int id;
    public boolean isDeleted;
    public String problemName;
    public Object productId;
    public SubjectGroup subjectGroup;
    public Teacher[] teachers;
    public int weight;

    public static class SubjectGroup {
        public int id;
        public String name;

        @Override
        public String toString() {
            return "SubjectGroup{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public static class Teacher {
        public int id;
        public String name;

        @Override
        public String toString() {
            return "Teacher{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DetailedAssignment{" +
                "activityName='" + activityName + '\'' +
                ", assignmentName='" + assignmentName + '\'' +
                ", attachments=" + Arrays.toString(attachments) +
                ", date='" + date + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", isDeleted=" + isDeleted +
                ", problemName='" + problemName + '\'' +
                ", productId=" + productId +
                ", subjectGroup=" + subjectGroup +
                ", teachers=" + Arrays.toString(teachers) +
                ", weight=" + weight +
                '}';
    }
}
