package me.theentropyshard.netschoolapi.schemas;

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
