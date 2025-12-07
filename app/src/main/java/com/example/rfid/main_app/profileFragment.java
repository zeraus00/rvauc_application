package com.example.rfid.main_app;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rfid.R;

public class profileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public profileFragment() {
    }

    public static profileFragment newInstance(String param1, String param2) {
        profileFragment fragment = new profileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchAndLoadProfileData(view);
    }

    private void setRowDetails(View rootView, int labelId, String labelText, int valueId, String valueText) {
        TextView label = rootView.findViewById(labelId);
        TextView value = rootView.findViewById(valueId);

        if (label != null) label.setText(labelText);
        if (value != null) value.setText(valueText);
    }

    private void fetchAndLoadProfileData(View rootView) {

        Activity activity = getActivity();
        if (activity == null) return;

        // Sample data
        String fullName = "Bogard D. Relapse";
        String studentNo = "231-44223";
        String gender = "Male";
        String department = "College of Computer Studies";
        String yearLevel = "3rd Year";
        String block = "1A";
        String email = "sampleEmail@example.com";
        String contactNumber = "0917-XXX-XXXX";

        populateUI(rootView, fullName, studentNo, gender, department, yearLevel, block, email, contactNumber);

        Toast.makeText(getContext(), "Profile data loaded successfully!", Toast.LENGTH_SHORT).show();
    }

    private void populateUI(View rootView,
                            String fullName,
                            String studentNo,
                            String gender,
                            String department,
                            String yearLevel,
                            String block,
                            String email,
                            String contactNumber) {

        setRowDetails(rootView, R.id.tvLabelName, "Full Name:", R.id.tvValueName, fullName);
        setRowDetails(rootView, R.id.tvLabelStudentNo, "Student Number:", R.id.tvValueStudentNo, studentNo);
        setRowDetails(rootView, R.id.tvLabelGender, "Gender:", R.id.tvValueGender, gender);

        setRowDetails(rootView, R.id.tvLabelDepartment, "Department:", R.id.tvValueDepartment, department);
        setRowDetails(rootView, R.id.tvLabelYearLevel, "Year Level:", R.id.tvValueYearLevel, yearLevel);
        setRowDetails(rootView, R.id.tvLabelBlock, "Block:", R.id.tvValueBlock, block);

        setRowDetails(rootView, R.id.tvLabelEmail, "Email:", R.id.tvValueEmail, email);
        setRowDetails(rootView, R.id.tvLabelContactNumber, "Contact Number:", R.id.tvValueContactNumber, contactNumber);

        ImageView imgProfile = rootView.findViewById(R.id.imgProfilePicture);
        imgProfile.setImageResource(R.drawable.nikki);
        }
    }

