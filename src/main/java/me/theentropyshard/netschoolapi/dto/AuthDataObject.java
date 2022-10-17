package me.theentropyshard.netschoolapi.dto;

import java.util.Objects;

public class AuthDataObject {
    public String pw;
    public String pw2;
    public int lt;
    public int ver;

    public AuthDataObject() {
    }

    public AuthDataObject(String pw, String pw2, int lt, int ver) {
        this.pw = pw;
        this.pw2 = pw2;
        this.lt = lt;
        this.ver = ver;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        AuthDataObject that = (AuthDataObject) o;
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
