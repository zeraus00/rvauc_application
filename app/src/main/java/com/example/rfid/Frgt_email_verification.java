package com.example.rfid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Frgt_email_verification extends AppCompatActivity {

    private TextView textCountdown1;
    private TextView resendText;
    private CountDownTimer countDownTimer;
    private Button verifying_button1;

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

        textCountdown1 = findViewById(R.id.textCountdown1);
        resendText = findViewById(R.id.textView18); // "Resend" TextView
        ImageButton backBtn = findViewById(R.id.backButton);

        // Back button listener
        backBtn.setOnClickListener(v -> {
            Intent backIntent = new Intent(Frgt_email_verification.this, LoginActivity.class);
            startActivity(backIntent);
            finish();
        });

        startCountdown(); // Start timer on load

        // Handle “Resend” tap to restart the timer
        resendText.setOnClickListener(v -> {
            if (countDownTimer != null) {
                countDownTimer.cancel(); // Stop current timer
            }
            startCountdown(); // Restart timer
            Toast.makeText(this, "Verification code resent!", Toast.LENGTH_SHORT).show();
        });
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(60000, 1000) { // 60 seconds
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                textCountdown1.setText("00:" + String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                textCountdown1.setText("00:00");
            }
        };
        countDownTimer.start();

        verifying_button1=findViewById(R.id.verifying_button1);
        verifying_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Frgt_email_verification.this, resetpass.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
