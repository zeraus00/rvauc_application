package com.example.rfid.main_app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rfid.R;

import java.util.ArrayList;
import java.util.List;

public class ClassFragment extends Fragment {

    LinearLayout classListContainer;

    List<ClassModel> classList = new ArrayList<>();

    public ClassFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_class, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        classListContainer = view.findViewById(R.id.class_list_container);

        loadSampleClasses();
        generateTableRows();
    }

    private void loadSampleClasses() {
        classList.add(new ClassModel("Prof. Santos", "Mobile Programming", "MP101"));
        classList.add(new ClassModel("Prof. Dela Cruz", "Data Structures", "DS103"));
        classList.add(new ClassModel("Prof. Reyes", "Operating Systems", "OS203"));
        classList.add(new ClassModel("Prof. Cruz", "Web Development", "WEB202"));
    }

    // -----------------------------------------------------------------
    // Create each row programmatically (NO NEED XML ROW TEMPLATE)
    // -----------------------------------------------------------------
    private void generateTableRows() {
        classListContainer.removeAllViews();

        for (ClassModel item : classList) {

            // Parent row
            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(10, 20, 10, 20);

            // Layout params for each cell
            LinearLayout.LayoutParams cellParams =
                    new LinearLayout.LayoutParams(0,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1);

            // --- Professor Cell ---
            TextView prof = new TextView(getContext());
            prof.setText(item.professor);
            prof.setTextColor(Color.parseColor("#333333"));
            prof.setLayoutParams(cellParams);

            // --- Class Name Cell ---
            TextView className = new TextView(getContext());
            className.setText(item.className);
            className.setTextColor(Color.parseColor("#333333"));
            className.setLayoutParams(cellParams);

            // --- Class Code Cell ---
            TextView classCode = new TextView(getContext());
            classCode.setText(item.classCode);
            classCode.setTextColor(Color.parseColor("#333333"));
            classCode.setGravity(Gravity.END);
            classCode.setLayoutParams(cellParams);

            // Add views to row
            row.addView(prof);
            row.addView(className);
            row.addView(classCode);

            // Click → open attendance fragment
            row.setOnClickListener(v -> openClassAttendance(item));

            // Add row to container
            classListContainer.addView(row);

            // Divider
            View divider = new View(getContext());
            divider.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1
                    ));
            divider.setBackgroundColor(Color.parseColor("#E0E0E0"));

            classListContainer.addView(divider);
        }
    }

    private void openClassAttendance(ClassModel classItem) {
        Fragment attendanceFragment = new ClassAttendanceFragment();

        Bundle bundle = new Bundle();
        bundle.putString("professor", classItem.professor);
        bundle.putString("className", classItem.className);
        bundle.putString("classCode", classItem.classCode);

        attendanceFragment.setArguments(bundle);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, attendanceFragment)
                .addToBackStack(null)
                .commit();
    }

    static class ClassModel {
        String professor;
        String className;
        String classCode;

        ClassModel(String professor, String className, String classCode) {
            this.professor = professor;
            this.className = className;
            this.classCode = classCode;
        }
    }
}
