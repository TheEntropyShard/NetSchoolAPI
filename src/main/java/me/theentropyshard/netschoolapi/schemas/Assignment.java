package me.theentropyshard.netschoolapi.schemas;

public class Assignment {
    public int id;
    public int typeId;
    public String assignmentName;
    public int weight;
    public String dueDate;
    public int classMeetingId;
    public Mark mark;

    public static class Mark {
        public int assignmentId;
        public int studentId;
        public int mark;
        public String resultScore;
        public boolean dutyMark;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + id +
                ", typeId=" + typeId +
                ", assignmentName='" + assignmentName + '\'' +
                ", weight=" + weight +
                ", dueDate='" + dueDate + '\'' +
                ", classMeetingId=" + classMeetingId +
                '}';
    }
}
