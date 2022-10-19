package me.theentropyshard.netschoolapi.schemas;

public class Announcement {
    public String description;
    public String postDate;
    public String deleteDate;
    public Author author;
    public String em;
    public String recipientInfo;
    public Attachment[] attachments;
    public int id;
    public String name;

    public static class Author {
        public int id;
        public String fio;
        public String nickName;
    }
}
