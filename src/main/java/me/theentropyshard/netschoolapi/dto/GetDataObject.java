package me.theentropyshard.netschoolapi.dto;

import java.util.Objects;

public class GetDataObject {
    public int lt;
    public String salt;
    public int ver;
    public String message;

    public GetDataObject() {
    }

    public GetDataObject(int lt, String salt, int ver) {
        this.lt = lt;
        this.salt = salt;
        this.ver = ver;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        GetDataObject that = (GetDataObject) o;
        return Objects.equals(lt, that.lt) && Objects.equals(salt, that.salt) && Objects.equals(ver, that.ver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lt, salt, ver);
    }

    @Override
    public String toString() {
        return "GetDataObject{" +
                "lt='" + lt + '\'' +
                ", salt='" + salt + '\'' +
                ", ver='" + ver + '\'' +
                '}';
    }
}
