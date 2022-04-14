package Scraper;

public class SubjectRow {
    private String name;
    private String code;
    private String seminarProf;
    private String lecturesProf;
    private Integer ECTS;
    private String term;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSeminarProf() {
        return seminarProf;
    }

    public void setSeminarProf(String seminarProf) {
        this.seminarProf = seminarProf;
    }

    public String getLecturesProf() {
        return lecturesProf;
    }

    public void setLecturesProf(String lecturesProf) {
        this.lecturesProf = lecturesProf;
    }

    public Integer getECTS() {
        return ECTS;
    }

    public void setECTS(Integer ECTS) {
        this.ECTS = ECTS;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String toString(){
        return name;
    }

}
