package com.example.rfid.main_app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.rfid.R;

import java.util.ArrayList;
import java.util.List;

public class ClassAttendanceFragment extends Fragment {

    private LinearLayout tableContainer;

    private ConstraintLayout presentFilter, absentFilter, excusedFilter, remainingFilter;

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
        remainingFilter = view.findViewById(R.id.btnRemaining);

        tableContainer = view.findViewById(R.id.tableContent);

        loadSampleRecords();

        presentFilter.setOnClickListener(v -> filterTable("Present"));
        absentFilter.setOnClickListener(v -> filterTable("Absent"));
        excusedFilter.setOnClickListener(v -> filterTable("Excused"));
        remainingFilter.setOnClickListener(v -> filterTable("Remaining"));

        displayAllRows();

        return view;
    }

    private void loadSampleRecords() {
        records.clear();

        records.add(new AttendanceRecord("11/20/25", "Mon", "9:00 - 10:00", "Present"));
        records.add(new AttendanceRecord("11/21/25", "Tue", "9:00 - 10:00", "Absent"));
        records.add(new AttendanceRecord("11/22/25", "Wed", "9:00 - 10:00", "Present"));
        records.add(new AttendanceRecord("11/23/25", "Thu", "9:00 - 10:00", "Excused"));
        records.add(new AttendanceRecord("11/24/25", "Fri", "9:00 - 10:00", "Remaining"));
        records.add(new AttendanceRecord("11/25/25", "Sat", "9:00 - 10:00", "Present"));
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
