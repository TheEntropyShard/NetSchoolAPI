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

public class UserSession {
    public int schoolId;
    public String eoName;
    public String at;
    public int userId;
    public String loginName;
    public String nickName;
    public String loginTime;
    public String lastAccessTime;
    public String ip;
    public String roles;
    public String eMs;
    public int timeOut;

    @Override
    public String toString() {
        return "UserSession{" +
                "schoolId=" + schoolId +
                ", eoName='" + eoName + '\'' +
                ", at='" + at + '\'' +
                ", userId=" + userId +
                ", loginName='" + loginName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", loginTime='" + loginTime + '\'' +
                ", lastAccessTime='" + lastAccessTime + '\'' +
                ", ip='" + ip + '\'' +
                ", roles='" + roles + '\'' +
                ", eMs='" + eMs + '\'' +
                ", timeOut=" + timeOut +
                '}';
    }
}
