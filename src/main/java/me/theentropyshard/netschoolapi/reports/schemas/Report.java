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
