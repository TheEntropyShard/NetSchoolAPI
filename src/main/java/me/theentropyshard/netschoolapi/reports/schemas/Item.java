package me.theentropyshard.netschoolapi.reports.schemas;

public class Item {
    public String title;
    public String value;

    @Override
    public String toString() {
        return "Item{" +
                "title='" + title + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
