package Scraper;

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
    private List<SubjectRow> subjectTable;
    private Map<String, String> cookies;
    private Map<String, String> loggedOnCookies;
    public Map<String, Integer> subjectsById;

    private String username;
    private String password;

    public boolean status;

    final String homeUrl = "https://ums.cit.edu.al/index.php";
    final String loginUrl = "https://ums.cit.edu.al/index.php?signIn=1";
    final String registerUrl = "https://ums.cit.edu.al/eRegisterData_view.php";
    final String profileUrl = "https://ums.cit.edu.al/membership_profile.php";
    final String preRegisterUrl = "https://ums.cit.edu.al/ERegisterStudents_view.php";

    //UniversityManagementSystem_remember_me cookie name for persistent login

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ScrapeWebsite(String username, String password, boolean rememberMe) throws IOException {
        subjectsById = new HashMap<>();
        mainVars.userName = username;
        this.status = true;
        this.username = username;
        this.password = password;

        this.subjectTable = new ArrayList<>();

        Connection.Response initialRequest = Jsoup.connect(loginUrl)
                .timeout(100000)
                .method(Connection.Method.GET)
                .execute();

        cookies = initialRequest.cookies();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Cookie", "UniversityManagementSystem="
                +cookies.get("UniversityManagementSystem"));
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Connection", "keep-alive");

        Map<String, String> data = new HashMap<String,String>();
        data.put("username", this.username);
        data.put("password", this.password);
        data.put("rememberMe", rememberMe?"1":"0");
        data.put("signIn", "signIn");

        Connection.Response login = Jsoup.connect(homeUrl)
                .timeout(100000)
                .headers(headers)
                .userAgent("PostmanRuntime/7.29.0")
                .data(data)
                .method(Connection.Method.POST)
                .execute();

        loggedOnCookies = login.cookies();

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
            //scrapePreRegister();
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

            gradeRow.setClassName(columns.get(1).text().substring(mainVars.fullName.length(), columns.get(1).text().indexOf('-') + 3));
            gradeRow.setAcademicYear(columns.get(1).text().substring(columns.get(1).text().indexOf('-') + 3));

            gradeRow.setGradeType(columns.get(2).text());
            if(columns.get(4).text().equals(""))
                gradeRow.setGradeWeight(0);
            else
                gradeRow.setGradeWeight(Float.parseFloat(columns.get(3).text()));
            if(columns.get(4).text().equals(""))
                gradeRow.setGrade(0);
            else
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

    //RIP fast access time, access to ERegisterStudents table was removed :(
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
    public List<GradeRow> getSubjectGrades(String subject) throws IOException {
        Optional<String> subjectOp = Optional.of(subject);
        return mainVars.gradeRows.stream().filter(x->x.getClassName().equals(subjectOp.orElse("ERROR"))).collect(Collectors.toList());
    }

    public String getRememberMe(){
        if(loggedOnCookies.size() > 1)
        return loggedOnCookies.get("UniversityManagementSystem_remember_me");
        else return "NONE";
    }

}
