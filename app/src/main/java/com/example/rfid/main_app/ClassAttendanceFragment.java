package com.example.rfid.main_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.rfid.R;

public class ClassAttendanceFragment extends Fragment {

    public ClassAttendanceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_attendance, container, false);

        LinearLayout absentLayout = view.findViewById(R.id.absent1);
        ConstraintLayout absentfilter = view.findViewById(R.id.absentfilter);

        absentLayout.setOnClickListener(v -> {
            Fragment appealFragment = new AppealFragment();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, appealFragment)
                    .addToBackStack(null)
                    .commit();
        });

        absentfilter.setOnClickListener(v -> {
            Fragment attdFilterFragment = new attdFilterFragment();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, attdFilterFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
