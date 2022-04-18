package com.example.ums_fix_java;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import Scraper.GradeRow;
import Scraper.ScrapeWebsite;
import Scraper.SubjectRow;

public class ProfileFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, null);
        return root;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView name = view.findViewById(R.id.Nameprf);
        TextView surname = view.findViewById(R.id.Surnameprf);
        TextView GPA = view.findViewById(R.id.GPAprf);
        TextView percentage = view.findViewById(R.id.Percentageprf);
        TextView grade = view.findViewById(R.id.Gradeprf);
        TextView email = view.findViewById(R.id.email);
        Button fetch = view.findViewById(R.id.fetchAVG);
        Spinner spinner = view.findViewById(R.id.spinnerprf);
        TextView nonSubs = view.findViewById(R.id.nonSubs);
        TextView cText = view.findViewById(R.id.ctext);

        //Get all academic years for which we have grades, create an ArrayAdapter object
        //which is used to set values in the spinner GUI widget
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                R.layout.spinner,
                ScrapeWebsite.getScraper().gradeRows.stream().map(GradeRow::getAcademicYear)
                        .distinct().collect(Collectors.toList()));
        spinner.setAdapter(adapter);

        //Set name, surname and email from scraping class
        String[] fullName = ScrapeWebsite.getScraper().fullName.split(" ");
        name.setText("Name: " + fullName[0]);
        surname.setText("Surname: " + fullName[1]);
        email.setText("Email: " + ScrapeWebsite.getScraper().userName);

        fetch.setOnClickListener(viewL -> {
            //Get the names all subjects retrieved by scraping at the beginning
            List<String> allSubjects = ScrapeWebsite.getScraper().subjectRows.stream().map(SubjectRow::toString).collect(Collectors.toList());

            //Get the names all subjects whose grades we have scraped
            List<String> gradedSubjects = ScrapeWebsite.getScraper().gradeRows.stream().map(GradeRow::toString).distinct().collect(Collectors.toList());

            //Get grades based on year chosen by user
            List<GradeRow> yearGrades = ScrapeWebsite.getScraper().gradeRows.stream()
                    .filter(x -> x.getAcademicYear().equals(spinner.getSelectedItem().toString()))
                    .collect(Collectors.toList());

            //Get classes with incomplete grades, concat with classes with no grades at all
            List<String> incompleteClasses = Stream.concat(yearGrades.stream()
                                                                     .filter(GradeRow::isIncomplete)
                                                                     .map(GradeRow::getClassName)
                                                                     .distinct(),
                                                           allSubjects.stream()
                                                                      .filter(x->!gradedSubjects.contains(x)))
                                                                      .collect(Collectors.toList());

            //Calculate average of all grades, excluding incomplete subjects
            OptionalDouble totalGrade = yearGrades.stream()
                    .mapToDouble(x -> yearGrades.stream()
                            .filter(y -> x.getClassName().equals(y.getClassName()))
                            .filter(y -> !incompleteClasses.contains(x.getClassName()))
                            .mapToDouble(GradeRow::getGrade).sum()).distinct().average();

            //Write out all incomplete subjects
            nonSubs.setText(incompleteClasses.stream().collect(Collectors.joining(", ")));
            cText.setVisibility(View.VISIBLE);
            GPA.setVisibility(View.VISIBLE);
            //Calculate GPA
            GPA.setText("GPA: " + new DecimalFormat("#.0").format(totalGrade.getAsDouble() / 100 * 4));
            percentage.setVisibility(View.VISIBLE);
            //Calculate percentage
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
        else return ("Grade: ERROR");
    }

}