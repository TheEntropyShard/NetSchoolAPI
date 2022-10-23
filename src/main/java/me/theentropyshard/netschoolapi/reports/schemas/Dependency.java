package me.theentropyshard.netschoolapi.reports.schemas;

public class Dependency {
    public RelatedObject relatedObject;
    public Object relatedValue;
    public String condition;

    @Override
    public String toString() {
        return "Dependency{" +
                "relatedObject=" + relatedObject +
                ", relatedValue=" + relatedValue +
                ", condition='" + condition + '\'' +
                '}';
    }
}
