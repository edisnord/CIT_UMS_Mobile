package com.example.ums_fix_java;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Scraper.GradeRow;
import Scraper.ScrapeWebsite;
import Scraper.SubjectRow;

public class GradesFragment extends Fragment {

    double totalGrade;
    double totalAchieved;
    double undefinedPercentage;

    List<GradeRow> tableRows;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_grades, null);
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);

        totalGrade = 0;
        totalAchieved = 0;
        undefinedPercentage = 0;

        LinearLayout averages = view.findViewById(R.id.linearLayout);
        averages.setVisibility(View.INVISIBLE);

        Spinner spinner = view.findViewById(R.id.spinner);
        List<String> subjectNames = ScrapeWebsite.subjectRows.stream().map(SubjectRow::toString)
                .distinct().collect(Collectors.toList());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                R.layout.spinner,
                subjectNames);

        spinner.setAdapter(adapter);

        Button fetch = view.findViewById(R.id.fetch);
        fetch.setOnClickListener(viewL -> {
                    try {
                        averages.setVisibility(View.VISIBLE);
                        tableRows = ScrapeWebsite.getScraper().getSubjectGrades(spinner.getSelectedItem().toString());
                        totalGrade = tableRows.stream().mapToDouble(GradeRow::getGradeWeight).reduce(0, Double::sum);
                        totalAchieved = tableRows.stream().mapToDouble(GradeRow::getGrade).reduce(0, Double::sum);

                        undefinedPercentage = 100 - totalGrade;

                        TextView gpa = view.findViewById(R.id.GPA);
                        TextView grade = view.findViewById(R.id.Grade);
                        TextView percentage = view.findViewById(R.id.Percentage);
                        if(undefinedPercentage == 0) {
                            gpa.setText("GPA: " + new DecimalFormat("#.0").format(totalAchieved / 100 * 4));
                            percentage.setText("Percentage: " + new DecimalFormat("#.0").format(totalAchieved) + "%");
                            grade.setText(getGrade());
                        } else{
                            gpa.setText("UNDEFINED");
                            percentage.setText("UNDEFINED");
                            grade.setText("UNDEFINED");
                        }
                        table(view);

                    } catch (IOException e) {
                        Toast toast = Toast.makeText(view.getContext(), "??? Error", Toast.LENGTH_SHORT);
                        toast.show();
                        e.printStackTrace();
                    }
                }
        );

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.N)
    void table(View view){
        TableLayout table = view.findViewById(R.id.table);

        ArrayList<View> children = getAllChildren(table);
        if(!children.isEmpty()){
            table.removeAllViews();
        }
        table.setStretchAllColumns(true);
        TableRow header = new TableRow(view.getContext());
        TextView col1 = new TextView(view.getContext());
        col1.setText("Grade Type");
        col1.setBackground(view.getContext().getDrawable(R.drawable.cell_header));
        header.addView(col1);
        TextView col2 = new TextView(view.getContext());
        col2.setText("Grade weight");
        col2.setBackground(view.getContext().getDrawable(R.drawable.cell_header));
        header.addView(col2);
        TextView col3 = new TextView(view.getContext());
        col3.setText("Your grade");
        col3.setBackground(view.getContext().getDrawable(R.drawable.cell_header));
        header.addView(col3);
        TextView col4 = new TextView(view.getContext());
        col4.setText("Date taken");
        col4.setBackground(view.getContext().getDrawable(R.drawable.cell_header));
        header.addView(col4);
        table.addView(header);
        for (GradeRow gr: tableRows) {
            TableRow tabRow = new TableRow(view.getContext());
            TextView tabCol1 = new TextView(view.getContext());
            tabCol1.setText(gr.getGradeType());
            tabCol1.setBackground(view.getContext().getDrawable(R.drawable.cell_rows));
            tabRow.addView(tabCol1);
            TextView tabCol2 = new TextView(view.getContext());
            if(gr.getGradeWeight() == 0) tabCol2.setText("Incomplete");
            else tabCol2.setText(Float.toString(gr.getGradeWeight()));
            tabCol2.setBackground(view.getContext().getDrawable(R.drawable.cell_rows));
            tabRow.addView(tabCol2);
            TextView tabCol3 = new TextView(view.getContext());
            if(gr.getGradeWeight() == 0) tabCol3.setText("Incomplete");
            else tabCol3.setText(Float.toString(gr.getGrade()));
            tabCol3.setBackground(view.getContext().getDrawable(R.drawable.cell_rows));
            tabRow.addView(tabCol3);
            TextView tabCol4 = new TextView(view.getContext());
            tabCol4.setText(gr.getDateTaken());
            tabCol4.setBackground(view.getContext().getDrawable(R.drawable.cell_rows));
            tabRow.addView(tabCol4);
            table.addView(tabRow);
        }

        if(undefinedPercentage > 0){
            TableRow undefRow = new TableRow(view.getContext());
            TextView tvr0 = new TextView(view.getContext());
            tvr0.setText("To be added");
            tvr0.setBackground(view.getContext().getDrawable(R.drawable.cell_rows));
            undefRow.addView(tvr0);
            TextView tvr1 = new TextView(view.getContext());
            tvr1.setText(Double.toString(undefinedPercentage));
            tvr1.setBackground(view.getContext().getDrawable(R.drawable.cell_rows));
            undefRow.addView(tvr1);
            TextView tvr2 = new TextView(view.getContext());
            tvr2.setText(Double.toString(undefinedPercentage));
            tvr2.setBackground(view.getContext().getDrawable(R.drawable.cell_rows));
            undefRow.addView(tvr2);
            TextView tvr3 = new TextView(view.getContext());
            tvr3.setText("NO DATA");
            tvr3.setBackground(view.getContext().getDrawable(R.drawable.cell_rows));
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