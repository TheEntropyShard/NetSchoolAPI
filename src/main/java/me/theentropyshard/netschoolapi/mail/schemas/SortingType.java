package me.theentropyshard.netschoolapi.mail.schemas;

public enum SortingType {
    SORT_DESC("Sent%20DESC");

    public final String VALUE;

    SortingType(String value) {
        this.VALUE = value;
    }
}
