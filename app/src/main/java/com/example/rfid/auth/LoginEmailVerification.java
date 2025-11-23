package com.example.rfid.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rfid.R;

public class LoginEmailVerification extends AppCompatActivity {

    private TextView textCountdown;
    private TextView resendText;
    private CountDownTimer countDownTimer;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_e_verification);

        // Handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textCountdown = findViewById(R.id.txtCountdown);
        resendText = findViewById(R.id.txtResendCode); // your “Resend” text
        ImageButton backBtn = findViewById(R.id.backButton);
        loginBtn = findViewById(R.id.btnVerifying1);

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginEmailVerification.this, LoginActivity.class));
            finish();
        });

        startCountdown(); // Start the initial 60s timer

        // Reset timer when "Resend" text is tapped
        resendText.setOnClickListener(v -> {
            if (countDownTimer != null) {
                countDownTimer.cancel(); // stop current timer
            }
            startCountdown(); // start a new 60s timer
            Toast.makeText(this, "Verification code resent!", Toast.LENGTH_SHORT).show();
        });

        loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginEmailVerification.this, EmailVerifiedSuccessful.class));
            finish();
        });
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(60000, 1000) { // 60 seconds
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                textCountdown.setText("00:" + String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                textCountdown.setText("00:00");
            }
        };
        countDownTimer.start();
    }
}
