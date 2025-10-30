package com.example.rfid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Frgt_email_verification extends AppCompatActivity {

    private TextView textCountdown1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_frgt_email_verification);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize countdown text
        textCountdown1 = findViewById(R.id.textCountdown1);
        ImageButton backBtn = findViewById(R.id.backButton);

        // Back button listener
        backBtn.setOnClickListener(v -> {
            Intent backIntent = new Intent(Frgt_email_verification.this, MainActivity.class);
            startActivity(backIntent);
            finish();
        });

        // 1-minute countdown (60,000 ms)
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                textCountdown1.setText("00:" + String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                textCountdown1.setText("Time’s up!");
            }
        }.start();
    }
}
