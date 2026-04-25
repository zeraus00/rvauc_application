package com.example.rfid.main_app;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rfid.R;
import com.example.rfid.features.enrollments.schemas.classattendance.AttendanceSummary;
import com.example.rfid.features.enrollments.schemas.classattendance.ClassAttendance;
import com.example.rfid.features.enrollments.services.EnrollmentsService;
import com.example.rfid.interfaces.HttpCallback;

public class ClassAttendanceFragment extends Fragment {
    private TextView classNumberCourseCode, courseName, professor;
    private TextView presentCounter, absentCounter, excusedCounter, lateCounter;

    public ClassAttendanceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_class_attendance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadViews(view);

        Bundle bundle = getArguments();

        if (bundle == null) return;
        loadUiFromBundle(bundle);
        int classId = getArguments().getInt("classId");

        EnrollmentsService.getAttendanceList(classId, new HttpCallback<EnrollmentsService.ClassAttendanceResponse>() {
            @Override
            public void onSuccess(EnrollmentsService.ClassAttendanceResponse response) {
                Fragment fragment = ClassAttendanceFragment.this;
                Activity activity = fragment.getActivity();

                if (activity == null) return;

                activity.runOnUiThread(() -> {
                    if (!fragment.isAdded() || fragment.getContext() == null) return;

                    if (response.success) loadUiForAttendance(response.result);
                    else Toast.makeText(requireContext(), "Fail loading data: " + response.message, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                Fragment fragment = ClassAttendanceFragment.this;
                Activity activity = fragment.getActivity();
                if (activity == null) return;

                activity.runOnUiThread(() -> {
                    if (!fragment.isAdded() || fragment.getContext() == null) return;

                    Toast.makeText(fragment.getContext(), "Fail loading data: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadViews(View view) {
        classNumberCourseCode = view.findViewById(R.id.txtClassNumberCourseCode);
        courseName = view.findViewById(R.id.txtCourseName);
        professor = view.findViewById(R.id.txtInstructor);
        presentCounter = view.findViewById(R.id.countPresent);
        absentCounter = view.findViewById(R.id.countAbsent);
        excusedCounter = view.findViewById(R.id.countExcused);
        lateCounter = view.findViewById(R.id.countLate);
    }

    private void loadUiFromBundle(Bundle bundle) {
        var defaultStr = "N/A";
        classNumberCourseCode.setText(bundle.getString("classNumberCourseCode", defaultStr));
        courseName.setText(bundle.getString("courseName", defaultStr));
        professor.setText(bundle.getString("professor", defaultStr));
    }

    private void loadUiForAttendance(ClassAttendance classAttendance) {
        loadUiForSummary(classAttendance.summary);
    }

    private void loadUiForSummary(AttendanceSummary summary) {
        int inferredAbsentCount = summary.absent + summary.missingRecords;

        presentCounter.setText("" + summary.present);
        excusedCounter.setText("" + summary.excused);
        absentCounter.setText("" + inferredAbsentCount);
        lateCounter.setText("" + summary.late);
    }
}
