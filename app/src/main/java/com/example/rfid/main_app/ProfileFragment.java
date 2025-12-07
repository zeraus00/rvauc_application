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
import com.example.rfid.features.auth.dto.Payload;
import com.example.rfid.features.auth.services.SessionManager;

public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Payload payload;
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

        payload = SessionManager.getInstance().getPayload();
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
        String firstName = payload.getFirstName() + " ";
        String raw = payload.getMiddleName().strip();
        String middleName = raw.isBlank() ? "" : raw.charAt(0) + ". ";
        String surname = payload.getSurname();
        String fullName = firstName + middleName + surname;
        String studentNo = payload.getStudentNumber();
        String first = payload.getGender().charAt(0) + "";
        String gender = first.toUpperCase() + payload.getGender().substring(1);
        String department = payload.getDepartment();
        int yearLevel = payload.getYearLevel();
        String block = payload.getBlock();
        String email = payload.getEmail();
        String contactNumber = payload.getContactNumber();

        populateUI(rootView, fullName, studentNo, gender, department, String.valueOf(yearLevel), block, email, contactNumber);

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

