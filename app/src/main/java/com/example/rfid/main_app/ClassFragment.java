package com.example.rfid.main_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rfid.R;

public class ClassFragment extends Fragment {

    LinearLayout class1, class2, class3, class4;

    public ClassFragment() {

    }

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


        class1 = view.findViewById(R.id.class1);
        class2 = view.findViewById(R.id.class2);
        class3 = view.findViewById(R.id.class3);
        class4 = view.findViewById(R.id.class4);


        class1.setOnClickListener(v -> loadFragment(new ClassAttendanceFragment()));
        class2.setOnClickListener(v -> loadFragment(new ClassFragment()));
        class3.setOnClickListener(v -> loadFragment(new NotificationFragment()));
        class4.setOnClickListener(v -> loadFragment(new PolicyFragment()));
    }

    private void loadFragment(Fragment fragment) {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void clearFragment() {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new Fragment())
                .commit();
    }
}
