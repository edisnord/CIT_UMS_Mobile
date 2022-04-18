package Scraper;

import android.content.Context;
import android.os.Build;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ScrapeWebsite {

    private Map<String, String> cookies;
    private static ScrapeWebsite scraper;

    public String userName;
    public String fullName;
    public List<GradeRow> gradeRows;
    public List<SubjectRow> subjectRows;

    final String homeUrl = "https://ums.cit.edu.al/index.php";
    final String loginUrl = "https://ums.cit.edu.al/index.php?signIn=1";
    final String registerUrl = "https://ums.cit.edu.al/eRegisterData_view.php";
    final String profileUrl = "https://ums.cit.edu.al/membership_profile.php";

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ScrapeWebsite getScraper() {
        if (scraper == null) {
            try {
                scraper = new ScrapeWebsite();
            } catch (IOException e) {
                System.out.println("Timeout, bad internet connection");
                e.printStackTrace();
            }
        }
        return scraper;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private ScrapeWebsite() throws IOException {
        gradeRows = new ArrayList<>();
        subjectRows = new ArrayList<>();
        userName = "";
        fullName = "";

        Connection.Response initialRequest = Jsoup.connect(loginUrl)
                .timeout(100000)
                .method(Connection.Method.GET)
                .execute();

        cookies = initialRequest.cookies();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void login(String username, String password) throws IOException {
        //Save email to variable
        this.userName = username;

        //Add headers for login request
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Cookie", "UniversityManagementSystem="
                + cookies.get("UniversityManagementSystem"));
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Connection", "keep-alive");

        //Add login form data
        Map<String, String> data = new HashMap<String, String>();
        data.put("username", username);
        data.put("password", password);
        data.put("rememberMe", "0");
        data.put("signIn", "signIn");

        //Perform login POST request
        Connection.Response login = Jsoup.connect(homeUrl)
                .timeout(100000)
                .headers(headers)
                .userAgent("PostmanRuntime/7.29.0")
                .data(data)
                .method(Connection.Method.POST)
                .execute();

        //Retrieve user profile page
        Connection.Response profileReq = Jsoup.connect(profileUrl)
                .timeout(100000)
                .header("Cookie", "UniversityManagementSystem=" + cookies.get("UniversityManagementSystem"))
                .userAgent("PostmanRuntime/7.29.0")
                .method(Connection.Method.GET)
                .execute();

        //Parse to JSoup document
        Document profilePage = profileReq.parse();
        //Get full name
        this.fullName = profilePage.getElementById("custom1").attr("value");

    }

    //Get new scraper instance
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void resetScraper() throws IOException {
        scraper = new ScrapeWebsite();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void scrapeGrades() throws IOException {

        //Connect to grades page
        Connection.Response gradesReq = Jsoup.connect(registerUrl)
                .timeout(100000)
                .header("Cookie", "UniversityManagementSystem=" + cookies.get("UniversityManagementSystem"))
                .userAgent("PostmanRuntime/7.29.0")
                .method(Connection.Method.GET)
                .execute();

        //Parse grades page, find table, get rows
        Document gradePage = gradesReq.parse();
        Elements elements = gradePage.getElementsByClass("table table-striped table-bordered table-hover");
        Elements rows = elements.get(0).select("tr");

        //Loop through rows, get data into GradeRow objects, put GradeRows into the ArrayList
        //gradeRows
        for (int i = 1; i < rows.size() - 1; i++) {
            Elements columns = rows.get(i).select("td");
            GradeRow gradeRow = new GradeRow();

            gradeRow.setClassName(columns.get(1).text().substring(this.fullName.length(), columns.get(1).text().indexOf('-') + 3));
            gradeRow.setAcademicYear(columns.get(1).text().substring(columns.get(1).text().indexOf('-') + 3));

            gradeRow.setGradeType(columns.get(2).text());
            if (columns.get(4).text().equals(""))
                gradeRow.setGradeWeight(0);
            else
                gradeRow.setGradeWeight(Float.parseFloat(columns.get(3).text()));
            if (columns.get(4).text().equals(""))
                gradeRow.setGrade(0);
            else
                gradeRow.setGrade(Float.parseFloat(columns.get(4).text()));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                if (!columns.get(5).text().equals(""))
                    gradeRow.setDateTaken(simpleDateFormat.parse(columns.get(5).text()));
            } catch (ParseException e) {
                System.out.println("couldn't parse date");
                e.printStackTrace();
            }

            gradeRow.setDateRegistered(columns.get(6).text());

            gradeRows.add(gradeRow);

        }

        System.out.println("Grades scraped!");

    }

    public void scrapeSubjects() throws IOException {
        final String subjectUrl = "https://ums.cit.edu.al/subjects_view.php";

        //Connect to subjects page
        Connection.Response gradesReq = Jsoup.connect(subjectUrl)
                .timeout(100000)
                .header("Cookie", "UniversityManagementSystem=" + cookies.get("UniversityManagementSystem"))
                .userAgent("PostmanRuntime/7.29.0")
                .method(Connection.Method.GET)
                .execute();

        //Parse, get table, get rows
        Document subjectPage = gradesReq.parse();
        Elements subjectElement = subjectPage.getElementsByClass("table table-striped table-bordered table-hover");
        Elements subjectRows = subjectElement.get(0).select("tr");

        //Loop through rows, extract data from columns to subjectRows
        for (int i = 1; i < subjectRows.size() - 1; i++) {
            Elements columns = subjectRows.get(i).select("td");
            SubjectRow subjectRow = new SubjectRow();

            subjectRow.setName(columns.get(1).text());
            subjectRow.setCode(columns.get(2).text());
            subjectRow.setSeminarProf(columns.get(3).text());
            subjectRow.setLecturesProf(columns.get(4).text());
            subjectRow.setECTS(Integer.parseInt(columns.get(5).text()));
            subjectRow.setTerm(columns.get(5).text());

            this.subjectRows.add(subjectRow);

        }


        System.out.println("Subjects scraped!");
    }

    //RIP fast access time, access to ERegisterStudents table was removed :(
    //Function to get grades for a specific subject from gradeRows, null safe
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<GradeRow> getSubjectGrades(String subject){
        return this.gradeRows.stream().filter(x ->
                x.getClassName().equals(Optional.of(subject).orElse("ERROR")))
                .collect(Collectors.toList());
    }

}
