package Scraper;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.Date;
import java.util.Optional;

public class GradeRow{
    private String className;
    private boolean incomplete;
    private String academicYear;
    private String gradeType;
    private float gradeWeight;
    private float grade;
    private Optional<Date> dateTaken;
    private String dateRegistered;

    @RequiresApi(api = Build.VERSION_CODES.N)
    GradeRow(){
        this.dateTaken = Optional.empty();
        this.incomplete = false;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getDateTaken() {
        String defaultRet = "NO DATA";
        return dateTaken.map(Date::toString)
                .map(x->x.substring(0, 10) + " " + x.substring(x.length() - 5))
                .orElse(defaultRet);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setDateTaken(Date dateTaken) {
        this.dateTaken = Optional.ofNullable(dateTaken);
    }

    public boolean isIncomplete(){
        return incomplete;
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
        if(gradeWeight == 0) incomplete = true;
    }

    public String toString(){
        return className;
    }

    public boolean equals(@NonNull GradeRow gradeRow){
        return gradeRow.getClassName().equals(this.getClassName());
    }

}
