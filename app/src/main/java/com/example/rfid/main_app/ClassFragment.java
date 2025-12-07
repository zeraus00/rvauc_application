package com.example.rfid.main_app;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rfid.R;
import com.example.rfid.features.enrollments.services.EnrollmentsService;
import com.example.rfid.interfaces.HttpCallback;

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

        EnrollmentsService.getClassList(new HttpCallback<EnrollmentsService.ClassListResponse>() {
            @Override
            public void onSuccess(EnrollmentsService.ClassListResponse response) {
                Fragment fragment = ClassFragment.this;
                Activity activity = fragment.getActivity();

                if (activity == null) return;

                activity.runOnUiThread(() -> {
                    if (!fragment.isAdded() || fragment.getContext() == null) return;

                    if (response.success) {
                        var result = response.result;

                        for(EnrollmentsService.ClassRecord _class : result.classList) {

                            var professor = _class.professor;
                            String professorName = "Prof. " + professor.surname;
                            classList.add(new ClassModel( professorName, _class.courseName,_class.classId, _class.classNumber, _class.courseCode, _class.weekDay, _class.startTimeText, _class.endTimeText));
                        }

                        generateTableRows();
                    }
                    else Toast.makeText(requireContext(), "Fail loading data: " + response.message, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                Fragment fragment = ClassFragment.this;
                Activity activity = fragment.getActivity();
                if (activity == null) return;

                activity.runOnUiThread(() -> {
                    if (!fragment.isAdded() || fragment.getContext() == null) return;

                    Toast.makeText(fragment.getContext(), "Fail loading data: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
        //loadSampleClasses();
        //generateTableRows();
    }

    private void loadSampleClasses() {
        classList.add(new ClassModel("Prof. Santos", "Mobile Programming", 1,"525","MP101", "", "", ""));
        classList.add(new ClassModel( "Prof. Dela Cruz", "Data Structures", 2, "526","DS103", "", "", ""));
        classList.add(new ClassModel("Prof. Reyes", "Operating Systems", 3, "527","OS203", "", "", ""));
        classList.add(new ClassModel("Prof. Cruz", "Web Development", 4, "528","WEB202", "", "", ""));
    }

    private void generateTableRows() {
        classListContainer.removeAllViews();

        for (ClassModel item : classList) {

            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(10, 20, 10, 20);

            // Layout params for each cell
            LinearLayout.LayoutParams cellParams =
                    new LinearLayout.LayoutParams(0,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1);

            TextView prof = new TextView(getContext());
            prof.setText(item.professor);
            prof.setTextColor(Color.parseColor("#333333"));
            prof.setLayoutParams(cellParams);

            TextView className = new TextView(getContext());
            className.setText(item.className);
            className.setTextColor(Color.parseColor("#333333"));
            className.setLayoutParams(cellParams);

            TextView classCode = new TextView(getContext());
            classCode.setText(item.classCode);
            classCode.setTextColor(Color.parseColor("#333333"));
            classCode.setGravity(Gravity.END);
            classCode.setLayoutParams(cellParams);

            row.addView(prof);
            row.addView(className);
            row.addView(classCode);

            row.setOnClickListener(v -> openClassAttendance(item));

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
        bundle.putInt("classId", classItem.classId);
        bundle.putString("className", classItem.className);
        bundle.putString("classNumber", classItem.classNumber);
        bundle.putString("classCode", classItem.classCode);
        bundle.putString("weekDay", classItem.weekDay);
        bundle.putString("startTime", classItem.startTime);
        bundle.putString("endTime", classItem.endTime);

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
        public int classId;
        String className;
        String classNumber;
        String classCode;

        String weekDay;
        String startTime;
        String endTime;

        ClassModel(String professor, String className, int classId, String classNumber, String classCode, String weekDay, String startTime, String endTime) {
            this.professor = professor;
            this.classId = classId;
            this.className = className;
            this.classNumber = classNumber;
            this.classCode = classCode;
            this.weekDay = weekDay;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
}
