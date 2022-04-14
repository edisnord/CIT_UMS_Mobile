package com.example.ums_fix_java;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Scraper.GradeRow;
import Scraper.SubjectRow;
import Scraper.mainVars;

public class GradesActivity extends AppCompatActivity {

    double totalGrade;
    double totalAchieved;
    double undefinedPercentage;

    ArrayList<GradeRow> tableRows;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        totalGrade = 0;
        totalAchieved = 0;
        undefinedPercentage = 0;

        setContentView(R.layout.activity_grades);
        Spinner spinner = findViewById(R.id.spinner);
        List<String> subjectNames = mainVars.subjectRows.stream().map(SubjectRow::toString)
                .distinct().collect(Collectors.toList());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner,
                subjectNames);

        spinner.setAdapter(adapter);

        Button fetch = findViewById(R.id.fetch);
        fetch.setOnClickListener(view -> {
                    try {
                        tableRows = mainVars.scraper.getSubjectGrades(spinner.getSelectedItem().toString());
                        totalGrade = tableRows.stream().mapToDouble(GradeRow::getGradeWeight).reduce(0, Double::sum);
                        totalAchieved = tableRows.stream().mapToDouble(GradeRow::getGrade).reduce(0, Double::sum);

                        undefinedPercentage = 100 - totalGrade;

                        TextView gpa = findViewById(R.id.GPA);
                        TextView grade = findViewById(R.id.Grade);
                        TextView percentage = findViewById(R.id.Percentage);
                        if(undefinedPercentage == 0) {
                            gpa.setText("GPA: " + new DecimalFormat("#.0").format(totalAchieved / 100 * 4));
                            percentage.setText("Percentage: " + new DecimalFormat("#.0").format(totalAchieved) + "%");
                            grade.setText(getGrade());
                        } else{
                            gpa.setText("UNDEFINED");
                            percentage.setText("UNDEFINED");
                            grade.setText("UNDEFINED");
                        }
                        table();

                    } catch (IOException e) {
                        Toast toast = Toast.makeText(getApplicationContext(), "??? Error", Toast.LENGTH_SHORT);
                        toast.show();
                        e.printStackTrace();
                    }
                }
        );

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void table(){
        TableLayout table = findViewById(R.id.table);

        ArrayList<View> children = getAllChildren(table);
        if(!children.isEmpty()){
            table.removeAllViews();
        }
        table.setStretchAllColumns(true);
        TableRow tbrow0 = new TableRow(this);
        TextView tv0 = new TextView(this);
        tv0.setText("Grade Type");
        tv0.setBackground(getDrawable(R.drawable.cell_header));
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText("Grade weight");
        tv1.setBackground(getDrawable(R.drawable.cell_header));
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText("Your grade");
        tv2.setBackground(getDrawable(R.drawable.cell_header));
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText("Date taken");
        tv3.setBackground(getDrawable(R.drawable.cell_header));
        tbrow0.addView(tv3);
        table.addView(tbrow0);
        for (GradeRow gr: tableRows) {
            TableRow tbrow1 = new TableRow(this);
            TextView tvr0 = new TextView(this);
            tvr0.setText(gr.getGradeType());
            tvr0.setBackground(getDrawable(R.drawable.cell_rows));
            tbrow1.addView(tvr0);
            TextView tvr1 = new TextView(this);
            tvr1.setText(Float.toString(gr.getGradeWeight()));
            tvr1.setBackground(getDrawable(R.drawable.cell_rows));
            tbrow1.addView(tvr1);
            TextView tvr2 = new TextView(this);
            tvr2.setText(Float.toString(gr.getGrade()));
            tvr2.setBackground(getDrawable(R.drawable.cell_rows));
            tbrow1.addView(tvr2);
            TextView tvr3 = new TextView(this);
            tvr3.setText(gr.getDateTaken());
            tvr3.setBackground(getDrawable(R.drawable.cell_rows));
            tbrow1.addView(tvr3);
            table.addView(tbrow1);
        }

        if(undefinedPercentage > 0){
            TableRow undefRow = new TableRow(this);
            TextView tvr0 = new TextView(this);
            tvr0.setText("To be added");
            tvr0.setBackground(getDrawable(R.drawable.cell_rows));
            undefRow.addView(tvr0);
            TextView tvr1 = new TextView(this);
            tvr1.setText(Double.toString(undefinedPercentage));
            tvr1.setBackground(getDrawable(R.drawable.cell_rows));
            undefRow.addView(tvr1);
            TextView tvr2 = new TextView(this);
            tvr2.setText(Double.toString(undefinedPercentage));
            tvr2.setBackground(getDrawable(R.drawable.cell_rows));
            undefRow.addView(tvr2);
            TextView tvr3 = new TextView(this);
            tvr3.setText("NO DATA");
            tvr3.setBackground(getDrawable(R.drawable.cell_rows));
            undefRow.addView(tvr3);
            table.addView(undefRow);
        }

    }

    private String getGrade(){
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

    private ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup vg = (ViewGroup) v;
        for (int i = 0; i < vg.getChildCount(); i++) {

            View child = vg.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }

}