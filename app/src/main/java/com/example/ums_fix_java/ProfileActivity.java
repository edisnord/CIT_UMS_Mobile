package com.example.ums_fix_java;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import java.text.DecimalFormat;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import Scraper.GradeRow;
import Scraper.ScrapeWebsite;
import Scraper.SubjectRow;
import Scraper.ScrapeWebsite;

public class ProfileActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        TextView name = findViewById(R.id.Nameprf);
        TextView surname = findViewById(R.id.Surnameprf);
        TextView GPA = findViewById(R.id.GPAprf);
        TextView percentage = findViewById(R.id.Percentageprf);
        TextView grade = findViewById(R.id.Gradeprf);
        TextView email = findViewById(R.id.email);
        Button fetch = findViewById(R.id.fetchAVG);
        Spinner spinner = findViewById(R.id.spinnerprf);
        TextView nonSubs = findViewById(R.id.nonSubs);
        androidx.appcompat.widget.Toolbar mToolbar = findViewById(R.id.toolbarprf);
        TextView cText = findViewById(R.id.ctext);

        List<String> termNames = ScrapeWebsite.gradeRows.stream().map(GradeRow::getAcademicYear)
                .distinct().collect(Collectors.toList());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner,
                termNames);

        spinner.setAdapter(adapter);

        String[] fullName = ScrapeWebsite.fullName.split(" ");
        name.setText("Name: " + fullName[0]);
        surname.setText("Surname: " + fullName[1]);
        email.setText("Email: " + ScrapeWebsite.userName);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fetch.setOnClickListener(view -> {
            //Get all subjects retrieved by scraping at the beginning
            List<String> allSubjects = ScrapeWebsite.subjectRows.stream().map(SubjectRow::toString).collect(Collectors.toList());

            //Get all subjects whose grades we have scraped
            List<String> gradedSubjects = ScrapeWebsite.gradeRows.stream().map(GradeRow::toString).distinct().collect(Collectors.toList());

            //Get grades based on element chosen by user
            List<GradeRow> yearGrades = ScrapeWebsite.gradeRows.stream()
                    .filter(x -> x.getAcademicYear().equals(spinner.getSelectedItem().toString()))
                    .collect(Collectors.toList());
            //Get classes with incomplete grades
            List<String> incompleteClasses = yearGrades.stream()
                    .filter(GradeRow::isIncomplete).map(GradeRow::getClassName)
                    .distinct()
                    .collect(Collectors.toList());

            incompleteClasses = Stream.concat(incompleteClasses.stream() , allSubjects.stream().filter(x->!gradedSubjects.contains(x))).collect(Collectors.toList());

            //Lists used in lambda expressions should be made final
            List<String> finalIncompleteClasses = incompleteClasses;

            //Calculate average of all grades, excluding incomplete subjects
            OptionalDouble totalGrade = yearGrades.stream()
                    .mapToDouble(x -> yearGrades.stream()
                            .filter(y -> x.getClassName().equals(y.getClassName()))
                            .filter(y -> !finalIncompleteClasses.contains(x.getClassName()))
                            .mapToDouble(GradeRow::getGrade).sum()).distinct().average();
            //Write out all incomplete subjects

            nonSubs.setText(incompleteClasses.stream().collect(Collectors.joining(", ")));
            cText.setVisibility(View.VISIBLE);
            GPA.setVisibility(View.VISIBLE);
            GPA.setText("GPA: " + new DecimalFormat("#.0").format(totalGrade.getAsDouble() / 100 * 4));
            percentage.setVisibility(View.VISIBLE);
            percentage.setText("Percentage: " + new DecimalFormat("#.0").format(totalGrade.getAsDouble()) + "%");
            grade.setVisibility(View.VISIBLE);
            grade.setText(getGrade(totalGrade.getAsDouble()));

        });

    }

    private String getGrade(Double totalAchieved){
        if (totalAchieved <= 100 && totalAchieved >= 90)
            return "Grade: A+";
        else if (totalAchieved < 90 && totalAchieved >= 80)
            return "Grade: A";
        else if (totalAchieved < 80 && totalAchieved >= 75)
            return ("Grade: B+");
        else if (totalAchieved < 75 && totalAchieved >= 70)
            return ("Grade: B");
        else if (totalAchieved < 70 && totalAchieved >= 65)
            return ("Grade: B-");
        else if (totalAchieved < 65 && totalAchieved >= 60)
            return ("Grade: C+");
        else if (totalAchieved < 60 && totalAchieved >= 55)
            return ("Grade: C");
        else if (totalAchieved < 55 && totalAchieved >= 50)
            return ("Grade: D");
        else if (totalAchieved < 50 && totalAchieved >= 0)
            return "Grade: F";
        else return ("what");
    }

}