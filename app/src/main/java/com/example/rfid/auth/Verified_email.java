package com.example.rfid.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rfid.R;
import com.example.rfid.home;

public class Verified_email extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verified_email);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btn_to_login = findViewById(R.id.verifying_btn);

        btn_to_login.setOnClickListener(v -> {
            startActivity(new Intent(Verified_email.this, home.class));
            finish();
        });

        ImageButton backBtn = findViewById(R.id.imageButton);

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(Verified_email.this, LoginActivity.class));
            finish();
        });
    }
}