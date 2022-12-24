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

package me.theentropyshard.netschoolapi.reports.models;

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
