package me.theentropyshard.netschoolapi.schemas;

public class Attachment {
    public int id;
    public String name;
    public String originalFileName;
    public String description;

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", originalFileName='" + originalFileName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
