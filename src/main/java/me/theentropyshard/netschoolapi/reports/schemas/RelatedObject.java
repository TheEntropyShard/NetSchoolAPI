package me.theentropyshard.netschoolapi.reports.schemas;

public class RelatedObject {
    public String type;
    public String ref;

    @Override
    public String toString() {
        return "RelatedObject{" +
                "type='" + type + '\'' +
                ", ref='" + ref + '\'' +
                '}';
    }
}
