package me.theentropyshard.netschoolapi.diary.models;

public class AssignmentType {
    public String abbr;
    public int id;
    public String name;
    public int order;

    @Override
    public String toString() {
        return "AssignmentType{" +
                "abbr='" + abbr + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", order=" + order +
                '}';
    }
}
