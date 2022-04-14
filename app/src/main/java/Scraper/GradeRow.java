package Scraper;

import java.util.Date;

public class GradeRow {
    private String className;
    private String academicYear;
    private String gradeType;
    private float gradeWeight;
    private float grade;
    private Date dateTaken;
    private String dateRegistered;

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(String dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getGradeType() {
        return gradeType;
    }

    public void setGradeType(String gradeType) {
        this.gradeType = gradeType;
    }

    public float getGradeWeight() {
        return gradeWeight;
    }

    public void setGradeWeight(float gradeWeight) {
        this.gradeWeight = gradeWeight;
    }
}
