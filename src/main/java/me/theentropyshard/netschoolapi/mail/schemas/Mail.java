package me.theentropyshard.netschoolapi.mail.schemas;

import java.util.Arrays;

public class Mail {
    public Message[] Records;
    public String Result;
    public int ResultStatus;
    public int TotalRecordCount;

    @Override
    public String toString() {
        return "Mail{" +
                "Records=" + Arrays.toString(Records) +
                ", Result='" + Result + '\'' +
                ", ResultStatus=" + ResultStatus +
                ", TotalRecordCount=" + TotalRecordCount +
                '}';
    }
}
