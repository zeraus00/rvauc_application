package com.example.rfid.main_app;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.rfid.R;


public class attdFilterFragment extends Fragment {

    public attdFilterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attd_filter, container, false);

        LinearLayout absentfil = view.findViewById(R.id.absentfil);

        absentfil.setOnClickListener(v -> {
            Fragment appealFragment = new AppealFragment();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, appealFragment)
                    .addToBackStack(null)
                    .commit();
        });



        return view;
    }
}