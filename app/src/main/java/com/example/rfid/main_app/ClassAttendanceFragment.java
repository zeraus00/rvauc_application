package com.example.rfid.main_app;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
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
import com.example.rfid.features.enrollments.schemas.classattendance.AttendanceSummary;
import com.example.rfid.features.enrollments.schemas.classattendance.ClassAttendance;
import com.example.rfid.features.enrollments.schemas.classweeklyschedule.ClassWeeklySchedule;
import com.example.rfid.features.enrollments.schemas.classweeklyschedule.WeeklyScheduleItem;
import com.example.rfid.features.enrollments.services.EnrollmentsService;
import com.example.rfid.interfaces.HttpCallback;

import java.util.Locale;

public class ClassAttendanceFragment extends Fragment {
    private TextView classNumberCourseCode, courseName, professor;
    private LinearLayout scheduleContainer;
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

        fetchAttendance(classId);
        fetchWeeklySchedule(classId);

    }

    private void loadViews(View view) {
        classNumberCourseCode = view.findViewById(R.id.txtClassNumberCourseCode);
        courseName = view.findViewById(R.id.txtCourseName);
        professor = view.findViewById(R.id.txtInstructor);

        scheduleContainer = view.findViewById(R.id.scheduleContainer);

        presentCounter = view.findViewById(R.id.countPresent);
        absentCounter = view.findViewById(R.id.countAbsent);
        excusedCounter = view.findViewById(R.id.countExcused);
        lateCounter = view.findViewById(R.id.countLate);
    }

    private void fetchAttendance(int classId) {
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

    private void fetchWeeklySchedule(int classId) {
        EnrollmentsService.getClassWeeklySchedule(classId, new HttpCallback<EnrollmentsService.ClassWeeklyScheduleResponse>() {
            @Override
            public void onSuccess(EnrollmentsService.ClassWeeklyScheduleResponse response) {
                Fragment fragment = ClassAttendanceFragment.this;
                Activity activity = fragment.getActivity();

                if (activity == null) return;

                activity.runOnUiThread(() -> {
                    if (!fragment.isAdded() || fragment.getContext() == null) return;

                    if (response.success) loadUiForSchedule(response.result);
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

    private void loadUiFromBundle(Bundle bundle) {
        var defaultStr = "N/A";
        classNumberCourseCode.setText(bundle.getString("classNumberCourseCode", defaultStr));
        courseName.setText(bundle.getString("courseName", defaultStr));
        professor.setText(bundle.getString("professor", defaultStr));
    }

    private void loadUiForSchedule(ClassWeeklySchedule cls) {
        scheduleContainer.removeAllViews();

        var context = getContext();

        for(WeeklyScheduleItem item: cls.schedule) {
            // ===== Left Column =====
            LinearLayout leftColumn = new LinearLayout(context);
            LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            );
            leftColumn.setLayoutParams(leftParams);
            leftColumn.setOrientation(LinearLayout.VERTICAL);
            leftColumn.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams wrapContentParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            // Weekday
            TextView dayText = new TextView(context);
            dayText.setLayoutParams(wrapContentParams);
            dayText.setText(item.weekDay.toUpperCase());
            dayText.setTextSize(12);
            dayText.setTypeface(null, Typeface.BOLD);
            dayText.setTextColor(Color.parseColor("#424242"));

            // Check in time
            TextView checkInTime = new TextView(context);
            LinearLayout.LayoutParams checkInTimeParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            checkInTimeParams.topMargin = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics()
            );
            checkInTime.setLayoutParams(checkInTimeParams);
            checkInTime.setText(item.startTime);
            checkInTime.setTextSize(14);
            checkInTime.setTypeface(null, Typeface.BOLD);
            checkInTime.setTextColor(Color.parseColor("#2E7D32"));

            // Check in label
            TextView checkInLabel = new TextView(context);
            checkInLabel.setLayoutParams(wrapContentParams);
            checkInLabel.setText("Check in");
            checkInLabel.setTextSize(11);
            checkInLabel.setTextColor(Color.parseColor("#757575"));

            // Check out time
            TextView checkOutTime = new TextView(context);
            LinearLayout.LayoutParams checkOutTimeParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            checkOutTimeParams.topMargin = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics()
            );
            checkOutTime.setLayoutParams(checkOutTimeParams);
            checkOutTime.setText(item.endTime);
            checkOutTime.setTextSize(14);
            checkOutTime.setTypeface(null, Typeface.BOLD);
            checkOutTime.setTextColor(Color.parseColor("#C62828"));

            // Check out label
            TextView checkOutLabel = new TextView(context);
            checkOutLabel.setLayoutParams(wrapContentParams);
            checkOutLabel.setText("Check out");
            checkOutLabel.setTextSize(11);
            checkOutLabel.setTextColor(Color.parseColor("#757575"));

            // Add views to left column
            leftColumn.addView(dayText);
            leftColumn.addView(checkInTime);
            leftColumn.addView(checkInLabel);
            leftColumn.addView(checkOutTime);
            leftColumn.addView(checkOutLabel);

            // ===== Divider =====
            View divider = new View(context);
            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                    (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics()
                    ),
                    (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 80, context.getResources().getDisplayMetrics()
                    )
            );
            dividerParams.gravity = Gravity.CENTER;
            divider.setLayoutParams(dividerParams);
            divider.setBackgroundColor(Color.parseColor("#CAD3CA"));

            // ===== Assemble =====
            scheduleContainer.addView(leftColumn);
            scheduleContainer.addView(divider);
        }
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
