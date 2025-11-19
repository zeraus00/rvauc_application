package com.example.rfid.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rfid.R;

public class resetpass extends AppCompatActivity {
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resetpass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button submit_Btn = findViewById(R.id.submitBtn);
        submit_Btn.setOnClickListener(v -> {
            startActivity(new Intent(resetpass.this, passupdt.class));
            finish();
        });

        Button Cancel_btn = findViewById(R.id.cancelBtn);
        Cancel_btn.setOnClickListener(v -> {
            startActivity(new Intent(resetpass.this, LoginActivity.class));
            finish();;
        });

        ImageButton backBtn = findViewById(R.id.imageButton);
        backBtn.setOnClickListener(v -> {
           startActivity(new Intent(resetpass.this, Frgt_email_verification.class));
           finish();
        });
    }
}