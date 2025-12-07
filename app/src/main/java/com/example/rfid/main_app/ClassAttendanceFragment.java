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
import com.example.rfid.features.enrollments.services.EnrollmentsService;
import com.example.rfid.interfaces.HttpCallback;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ClassAttendanceFragment extends Fragment {

    private LinearLayout tableContainer;

    private ConstraintLayout presentFilter, absentFilter, excusedFilter;

    private int presentCount = 0;
    private int absentCount = 0;
    private int excusedCount = 0;
    private int remainingCount = 5;

    private TextView presentCounter, absentCounter, excusedCounter, remainingCounter;

    private List<AttendanceRecord> records = new ArrayList<>();

    public ClassAttendanceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_class_attendance, container, false);

        presentFilter = view.findViewById(R.id.btnPresent);
        absentFilter = view.findViewById(R.id.btnAbsent);
        excusedFilter = view.findViewById(R.id.btnExcused);

        tableContainer = view.findViewById(R.id.tableContent);

        presentCounter = view.findViewById(R.id.countPresent);
        absentCounter = view.findViewById(R.id.countAbsent);
        excusedCounter = view.findViewById(R.id.countExcused);
        remainingCounter = view.findViewById(R.id.countRemaining);

        presentFilter.setOnClickListener(v -> filterTable("Present"));

        absentFilter.setOnClickListener(v -> filterTable("Absent"));

        excusedFilter.setOnClickListener(v -> filterTable("Excused"));


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() == null) return;

        int enrollmentId = getArguments().getInt("enrollmentId") ;
        Log.i("class_attendance", String.valueOf(enrollmentId));
        String weekDay = getArguments().getString("weekDay");
        String startTime = getArguments().getString("startTime");
        String endTime = getArguments().getString("endTime");

        EnrollmentsService.getAttendanceList(enrollmentId, new HttpCallback<EnrollmentsService.AttendanceListResponse>() {
            @Override
            public void onSuccess(EnrollmentsService.AttendanceListResponse response) {
                Fragment fragment = ClassAttendanceFragment.this;
                Activity activity = fragment.getActivity();

                if (activity == null) return;

                activity.runOnUiThread(() -> {
                    if (!fragment.isAdded() || fragment.getContext() == null) return;

                    if (response.success) {
                        var result = response.result;
                        Log.i("class_attendance", result.toString());

                        for (EnrollmentsService.AttendanceRecord record : result.attendanceList) {
                            Log.i("class_attendance", record.toString());
                            records.add(new AttendanceRecord(record.date, weekDay, record.time, record.status));
                        }

                        updateCounters();
                        displayAllRows();
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

    private void loadSampleRecords() {
        records.clear();

        records.add(new AttendanceRecord("11/20/25", "Mon", "9:00 - 10:00", "Present"));
        records.add(new AttendanceRecord("11/21/25", "Tue", "9:00 - 10:00", "Absent"));
        records.add(new AttendanceRecord("11/22/25", "Wed", "9:00 - 10:00", "Present"));
        records.add(new AttendanceRecord("11/23/25", "Thu", "9:00 - 10:00", "Excused"));
        records.add(new AttendanceRecord("11/25/25", "Sat", "9:00 - 10:00", "Present"));
    }

    private void updateCounters() {
        int present = 0;
        int absent = 0;
        int excused = 0;

        for (AttendanceRecord r : records) {
            String s = r.status.toLowerCase();

            if (s.equals("present")) {
                present++;
            }
            else if (s.equals("absent")) {
                absent++;
            }
            else if (s.equals("excused")) {
                excused++;
            }
        }

        int remaining = 5 - absent;

        presentCounter.setText(String.valueOf(present));
        absentCounter.setText(String.valueOf(absent));
        excusedCounter.setText(String.valueOf(excused));
        remainingCounter.setText(String.valueOf(Math.max(remaining, 0)));
    }

    private void filterTable(String filterStatus) {
        tableContainer.removeAllViews();

        for (AttendanceRecord record : records) {
            if (record.status.equalsIgnoreCase(filterStatus)) {
                addRow(record);
            }
        }
    }

    private void displayAllRows() {
        tableContainer.removeAllViews();
        for (AttendanceRecord record : records) {
            addRow(record);
        }
    }

    private void addRow(AttendanceRecord record) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(10, 22, 10, 22);
        row.setWeightSum(4);

        TextView date = createCell(record.date);
        TextView day = createCell(record.day);
        TextView hours = createCell(record.hours);
        TextView status = createCell(record.status);

        // Status color
        switch (record.status.toLowerCase()) {
            case "present":
                status.setTextColor(Color.parseColor("#00A86B"));
                break;
            case "absent":
                status.setTextColor(Color.parseColor("#D22B2B"));
                break;
            case "excused":
                status.setTextColor(Color.parseColor("#1E90FF"));
                break;
            case "remaining":
                status.setTextColor(Color.parseColor("#FF8C00"));
                break;
        }

        row.addView(date);
        row.addView(day);
        row.addView(hours);
        row.addView(status);

        if (record.status.equalsIgnoreCase("Absent")) {
            row.setOnClickListener(v -> {
                Fragment appealFragment = new AppealFragment();
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, appealFragment)
                        .addToBackStack(null)
                        .commit();
            });
        }

        tableContainer.addView(row);
    }

    private TextView createCell(String text) {
        TextView tv = new TextView(getContext());
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        ));
        tv.setGravity(Gravity.CENTER);
        tv.setText(text);
        tv.setTextSize(14);
        tv.setTextColor(Color.parseColor("#333333"));
        return tv;
    }

    public static class AttendanceRecord {
        String date;
        String day;
        String hours;
        String status;

        public AttendanceRecord(String date, String day, String hours, String status) {
            this.date = date;
            this.day = day;
            this.hours = hours;
            this.status = status;
        }
    }
}
