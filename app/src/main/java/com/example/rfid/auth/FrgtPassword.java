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

public class FrgtPassword extends AppCompatActivity {
    private ImageButton backButton;
    private Button btn_another;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FrgtPassword.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button verifying_btn = findViewById(R.id.button3);

        verifying_btn.setOnClickListener(v -> {
            Intent intent = new Intent(FrgtPassword.this, FrgtPassEmailVerification.class);
            startActivity(intent);
            finish();
        });
//
    }
}
