package Scraper;

import android.os.AsyncTask;
import android.os.Build;
import android.text.Editable;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import android.content.Context;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScrapeWebsite {
    private List<SubjectRow> subjectTable;
    private Map<String, String> cookies;
    public Map<String, Integer> subjectsById;

    private String username;
    private String password;

    public boolean status;

    final String baseUrl = "https://ums.cit.edu.al/index.php?signIn=1";
    final String registerUrl = "https://ums.cit.edu.al/eRegisterData_view.php";
    final String profileUrl = "https://ums.cit.edu.al/membership_profile.php";
    final String preRegisterUrl = "https://ums.cit.edu.al/ERegisterStudents_view.php";
            //"https://ums.cit.edu.al/parent-children.php"; //+
            //"?ChildTable=eRegisterData&ChildLookupField=RegisteredStudent&SelectedID=938&Page=1&SortBy&SortDirection&Operation=get-records";

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ScrapeWebsite(String username, String password) throws IOException {
        subjectsById = new HashMap<>();
        mainVars.userName = username;
        this.status = true;
        this.username = username;
        this.password = password;

        this.subjectTable = new ArrayList<>();

        Connection.Response initialRequest = Jsoup.connect(baseUrl)
                .timeout(100000)
                .method(Connection.Method.POST)
                .execute();

        cookies = initialRequest.cookies();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Cookie", "UniversityManagementSystem="+cookies.get("UniversityManagementSystem"));
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Connection", "keep-alive");

        Map<String, String> data = new HashMap<String,String>();
        data.put("username", username);
        data.put("password", password);
        data.put("rememberMe", "1");
        data.put("signIn", "signIn");

        Connection.Response login = Jsoup.connect("https://ums.cit.edu.al/index.php")
                .timeout(100000)
                .headers(headers)
                .userAgent("PostmanRuntime/7.29.0")
                .data(data)
                .method(Connection.Method.POST)
                .execute();

        if(login.statusCode() == 200){
            status = true;
            Connection.Response profileReq = Jsoup.connect(profileUrl)
                    .timeout(100000)
                    .header("Cookie", "UniversityManagementSystem="+cookies.get("UniversityManagementSystem"))
                    .userAgent("PostmanRuntime/7.29.0")
                    .method(Connection.Method.GET)
                    .execute();

            Document profilePage = profileReq.parse();
            mainVars.fullName = profilePage.getElementById("custom1").attr("value");

            run();

        } else {
            status = false;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void run() {
        try {
            scrapeGrades();
            scrapeSubjects();
            scrapePreRegister();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void scrapeGrades() throws IOException {
        List<GradeRow> gradeTable = new ArrayList<>();

        Connection.Response gradesReq = Jsoup.connect(registerUrl)
                .timeout(100000)
                .header("Cookie", "UniversityManagementSystem="+cookies.get("UniversityManagementSystem"))
                .userAgent("PostmanRuntime/7.29.0")
                .method(Connection.Method.GET)
                .execute();

        Document gradePage = gradesReq.parse();
        Elements elements = gradePage.getElementsByClass("table table-striped table-bordered table-hover");
        Elements rows = elements.get(0).select("tr");

        for (int i = 1; i < rows.size() - 1; i++) {
            Elements columns = rows.get(i).select("td");
            GradeRow gradeRow = new GradeRow();

            gradeRow.setClassName(columns.get(1).text().substring(mainVars.fullName.length(), columns.get(1).text().indexOf('-')));
            gradeRow.setAcademicYear(columns.get(1).text().substring(columns.get(1).text().indexOf('-') + 3));

            gradeRow.setGradeType(columns.get(2).text());
            gradeRow.setGradeWeight(Float.parseFloat(columns.get(3).text()));
            gradeRow.setGrade(Float.parseFloat(columns.get(4).text()));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            try {
                if(!columns.get(5).text().equals(""))
                    gradeRow.setDateTaken(simpleDateFormat.parse(columns.get(5).text().replace('/', '-')));
            } catch ( ParseException e) {
                System.out.println("couldn't parse date");
                e.printStackTrace();
            }

            gradeRow.setDateRegistered(columns.get(6).text());

            gradeTable.add(gradeRow);

        }

        if(!gradeTable.isEmpty()) mainVars.gradeRows = gradeTable;

        System.out.println("Grades scraped!");

    }

    private void scrapeSubjects() throws IOException {
        final String subjectUrl = "https://ums.cit.edu.al/subjects_view.php";

        Connection.Response gradesReq = Jsoup.connect(subjectUrl)
                .timeout(100000)
                .header("Cookie", "UniversityManagementSystem="+cookies.get("UniversityManagementSystem"))
                .userAgent("PostmanRuntime/7.29.0")
                .method(Connection.Method.GET)
                .execute();

        Document subjectPage = gradesReq.parse();

        Elements subjectElement = subjectPage.getElementsByClass("table table-striped table-bordered table-hover");
        Elements subjectRows = subjectElement.get(0).select("tr");

        for (int i = 1; i < subjectRows.size() - 1; i++) {
            Elements columns = subjectRows.get(i).select("td");
            SubjectRow gradeRow = new SubjectRow();

            gradeRow.setName(columns.get(1).text());
            gradeRow.setCode(columns.get(2).text());
            gradeRow.setSeminarProf(columns.get(3).text());
            gradeRow.setLecturesProf(columns.get(4).text());
            gradeRow.setECTS(Integer.parseInt(columns.get(5).text()));
            gradeRow.setTerm(columns.get(5).text());

            subjectTable.add(gradeRow);

        }

        if(!subjectTable.isEmpty()) mainVars.subjectRows = subjectTable;

        System.out.println("Subjects scraped!");
    }

    private void scrapePreRegister() throws IOException {


        Connection.Response IDs = Jsoup.connect(preRegisterUrl)
                .timeout(100000)
                .cookies(cookies)
                .userAgent("PostmanRuntime/7.29.0")
                .method(Connection.Method.GET)
                .execute();

        Document IDList = Jsoup.parse(IDs.body());
        Elements elements = IDList.getElementsByClass("table table-striped table-bordered table-hover");
        Elements rows = elements.get(0).select("tr");

        for (int i = 1; i < rows.size() - 1; i++) {
            rows.get(i).attr("data-id");
            Elements columns = rows.get(i).select("td");
            columns.get(2).text();
            subjectsById.put( columns.get(2).text().substring(0,columns.get(2).text().indexOf('-') + 3 ),
                    Integer.valueOf(rows.get(i).attr("data-id")));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<GradeRow> getSubjectGrades(String subject) throws IOException {
        ArrayList<GradeRow> gradeTable = new ArrayList<>();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Cookie", "UniversityManagementSystem="+cookies.get("UniversityManagementSystem"));
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Connection", "keep-alive");

        Map<String, String> data = new HashMap<String,String>();
        data.put("ChildTable", "eRegisterData");
        data.put("ChildLookupField", "RegisteredStudent");
        data.put("Page", "1");
        data.put("SortBy", "");
        data.put("SortDirection", "");
        data.put("Operation", "get-records");
        //Check for null ktu
        data.put("SelectedID", Integer.toString(subjectsById.get(subject)));

        Connection.Response getGrades = Jsoup.connect("https://ums.cit.edu.al/parent-children.php")
                .timeout(100000)
                .headers(headers)
                .userAgent("PostmanRuntime/7.29.0")
                .data(data)
                .method(Connection.Method.POST)
                .execute();

        Document gradePage = getGrades.parse();
        Elements elements = gradePage.getElementsByClass("table table-striped table-hover table-condensed table-bordered");
        Elements rows = elements.get(0).select("tr");

        for (int i = 1; i < rows.size() - 1; i++) {
            Elements columns = rows.get(i).select("td");
            GradeRow gradeRow = new GradeRow();

            gradeRow.setClassName(columns.get(1).text().substring(4, columns.get(1).text().indexOf('-', 5) + 3));
            gradeRow.setAcademicYear(columns.get(1).text().substring(gradeRow.getClassName().length() + 4,
                    columns.get(1).text().indexOf('-',
                            gradeRow.getClassName().length() + 3)+5));
            gradeRow.setGradeType(columns.get(2).text());
            gradeRow.setGradeWeight(Float.parseFloat(columns.get(3).text()));
            gradeRow.setGrade(Float.parseFloat(columns.get(4).text()));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            try {
                if(!columns.get(5).text().equals(""))
                    gradeRow.setDateTaken(simpleDateFormat.parse(columns.get(5).text().replace('/', '-')));
            } catch ( ParseException e) {
                System.out.println("couldn't parse date");
                e.printStackTrace();
            }

            gradeRow.setDateRegistered(columns.get(6).text());

            gradeTable.add(gradeRow);

        }

        return gradeTable;

    }

}
