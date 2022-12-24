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

public class Announcement {
    public String description;
    public String postDate;
    public String deleteDate;
    public Author author;
    public Em em;
    public String recipientInfo;
    public Attachment[] attachments;
    public int id;
    public String name;

    public static class Em {
        public int id;
        public String name;

        @Override
        public String toString() {
            return "Em{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public static class Author {
        public int id;
        public String fio;
        public String nickName;

        @Override
        public String toString() {
            return "Author{" +
                    "id=" + id +
                    ", fio='" + fio + '\'' +
                    ", nickName='" + nickName + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "description='" + description + '\'' +
                ", postDate='" + postDate + '\'' +
                ", deleteDate='" + deleteDate + '\'' +
                ", author=" + author +
                ", em=" + em +
                ", recipientInfo='" + recipientInfo + '\'' +
                ", attachments=" + Arrays.toString(attachments) +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
