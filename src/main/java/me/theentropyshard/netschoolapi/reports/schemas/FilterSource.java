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

public class FilterSource {
    public Item[] items;
    public String defaultValue;
    public String filterId;
    public Object nullText;
    public String minValue;
    public String maxValue;
    public String message;
    public DateRange range;
    public DateRange defaultRange;

    @Override
    public String toString() {
        return "FilterSource{" +
                "items=" + Arrays.toString(items) +
                ", defaultValue='" + defaultValue + '\'' +
                ", filterId='" + filterId + '\'' +
                ", nullText=" + nullText +
                ", minValue='" + minValue + '\'' +
                ", maxValue='" + maxValue + '\'' +
                ", message='" + message + '\'' +
                ", range=" + range +
                ", defaultRange=" + defaultRange +
                '}';
    }
}
