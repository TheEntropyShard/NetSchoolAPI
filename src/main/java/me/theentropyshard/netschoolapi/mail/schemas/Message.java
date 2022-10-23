package me.theentropyshard.netschoolapi.mail.schemas;

public class Message {
    public String FromEOName;
    public String FromName;
    public int MessageId;
    public String Read;
    public String Sent;
    public String SentTo;
    public String Subj;

    @Override
    public String toString() {
        return "Message{" +
                "FromEOName='" + FromEOName + '\'' +
                ", FromName='" + FromName + '\'' +
                ", MessageId=" + MessageId +
                ", Read='" + Read + '\'' +
                ", Sent='" + Sent + '\'' +
                ", SentTo='" + SentTo + '\'' +
                ", Subj='" + Subj + '\'' +
                '}';
    }
}
