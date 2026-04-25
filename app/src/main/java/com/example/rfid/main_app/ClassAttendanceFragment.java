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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.rfid.R;
import com.example.rfid.features.enrollments.schemas.classattendance.HistoryElement;
import com.example.rfid.features.enrollments.services.EnrollmentsService;
import com.example.rfid.interfaces.HttpCallback;

import java.util.ArrayList;
import java.util.List;

public class ClassAttendanceFragment extends Fragment {
    private TextView classNumberCourseCode, courseName, instructor;
    private TextView presentCounter, absentCounter, excusedCounter, remainingCounter;

    public ClassAttendanceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_class_attendance, container, false);

        loadViews(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() == null) return;
        int classId = getArguments().getInt("classId");

        EnrollmentsService.getAttendanceList(classId, new HttpCallback<EnrollmentsService.ClassAttendanceResponse>() {
            @Override
            public void onSuccess(EnrollmentsService.ClassAttendanceResponse response) {
                Fragment fragment = ClassAttendanceFragment.this;
                Activity activity = fragment.getActivity();

                if (activity == null) return;

                activity.runOnUiThread(() -> {
                    if (!fragment.isAdded() || fragment.getContext() == null) return;

                    if (response.success) {
                        var result = response.result;

                    }
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
        instructor = view.findViewById(R.id.txtInstructor);
        presentCounter = view.findViewById(R.id.countPresent);
        absentCounter = view.findViewById(R.id.countAbsent);
        excusedCounter = view.findViewById(R.id.countExcused);
        remainingCounter = view.findViewById(R.id.countRemaining);
    }

}
