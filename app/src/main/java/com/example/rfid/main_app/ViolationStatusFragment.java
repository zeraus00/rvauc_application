package com.example.rfid.main_app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rfid.R;
import com.example.rfid.adapters.ViolationAdapter;
import com.example.rfid.features.uniformcompliance.services.UniformComplianceService;
import com.example.rfid.features.uniformcompliance.services.ViolationDTO;
import com.example.rfid.interfaces.HttpCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViolationStatusFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_violation_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recycler = view.findViewById(R.id.violationRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        /*mockup data*/
        List<ViolationDTO> data = new ArrayList<>();

        ViolationDTO r1 = new ViolationDTO();
        r1.id = 1;
        r1.date = "2025-10-13";
        r1.day = "Monday";
        r1.time = "9:00 - 10:00";
        r1.reasons = Arrays.asList("incorrect footwear");

        ViolationDTO r2 = new ViolationDTO();
        r2.id = 2;
        r2.date = "2025-10-14";
        r2.day = "Tuesday";
        r2.time = "8:00 - 9:00";
        r2.reasons = Arrays.asList("no id", "incorrect upperwear");

        data.add(r1);
        data.add(r2);

        recycler.setAdapter(new ViolationAdapter(data));
    }

}
