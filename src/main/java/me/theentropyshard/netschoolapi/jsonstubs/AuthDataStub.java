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

package me.theentropyshard.netschoolapi.jsonstubs;

import java.util.Objects;

public class AuthDataStub {
    public String pw;
    public String pw2;
    public int lt;
    public int ver;

    public AuthDataStub() {
    }

    public AuthDataStub(String pw, String pw2, int lt, int ver) {
        this.pw = pw;
        this.pw2 = pw2;
        this.lt = lt;
        this.ver = ver;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        AuthDataStub that = (AuthDataStub) o;
        return Objects.equals(pw, that.pw) && Objects.equals(pw2, that.pw2) && Objects.equals(lt, that.lt) && Objects.equals(ver, that.ver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pw, pw2, lt, ver);
    }

    @Override
    public String toString() {
        return "AuthDataObject{" +
                "pw='" + pw + '\'' +
                ", pw2='" + pw2 + '\'' +
                ", lt='" + lt + '\'' +
                ", ver='" + ver + '\'' +
                '}';
    }
}
