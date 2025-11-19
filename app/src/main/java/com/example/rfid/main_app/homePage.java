package com.example.rfid.main_app;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.rfid.R;

public class homePage extends AppCompatActivity {
    ImageButton statsBtn, classBtn, homeBtn, notifBtn, policyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        statsBtn = findViewById(R.id.stats);
        classBtn = findViewById(R.id.btn_class);
        homeBtn = findViewById(R.id.btn_home);
        notifBtn = findViewById(R.id.btn_notif);
        policyBtn = findViewById(R.id.btn_policy);


        statsBtn.setOnClickListener(v -> loadFragment(new StatusFragment()));
        classBtn.setOnClickListener(v -> loadFragment(new ClassFragment()));
        notifBtn.setOnClickListener(v -> loadFragment(new NotificationFragment()));
        policyBtn.setOnClickListener(v -> loadFragment(new PolicyFragment()));

        homeBtn.setOnClickListener(v -> clearFragment());
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    public void clearFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new Fragment())
                .commit();
    }
}