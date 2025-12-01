package com.example.rfid.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rfid.R;
import com.example.rfid.dto.VoidResponse;
import com.example.rfid.features.auth.services.PasswordManagementService;
import com.example.rfid.features.auth.services.PasswordResetManager;
import com.example.rfid.interfaces.HttpCallback;

public class FrgtPassEmailVerification extends AppCompatActivity {
    private PasswordResetManager passwordResetManager;
    private String email;
    private TextView textCountdown1;
    private CountDownTimer countDownTimer;
    private EditText[] inputs;

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

        passwordResetManager = PasswordResetManager.getInstance();
        email = passwordResetManager.getEmail();

        TextView emailView = findViewById(R.id.textView14);
        emailView.setText(email);

        EditText input1 = findViewById(R.id.etInputBox1);
        EditText input2 = findViewById(R.id.etInputBox2);
        EditText input3 = findViewById(R.id.etInputBox3);
        EditText input4 = findViewById(R.id.etInputBox4);
        EditText input5 = findViewById(R.id.etInputBox5);
        EditText input6 = findViewById(R.id.etInputBox6);
        inputs = new EditText[]{input1, input2, input3, input4, input5, input6};

        textCountdown1 = findViewById(R.id.txtFrgtCountdown);
        Button submitBtn = findViewById(R.id.btnFrgtVerifying);
        TextView resendText = findViewById(R.id.txtFrgtResendCode); // "Resend" TextView
        ImageButton backBtn = findViewById(R.id.backButton);

        // Back button listener
        backBtn.setOnClickListener(v -> {
            passwordResetManager.setEmail(null);
            Intent backIntent = new Intent(FrgtPassEmailVerification.this, LoginActivity.class);
            startActivity(backIntent);
            finish();
        });

        startCountdown(); // Start timer on load

        submitBtn.setOnClickListener(v -> handleSubmit());
        // Handle “Resend” tap to restart the timer
        resendText.setOnClickListener(v -> handleResend());
    }

    private void handleResend() {
        PasswordManagementService.ForgotPasswordRequest request = new PasswordManagementService.ForgotPasswordRequest();
        request.email = email;

        PasswordManagementService.forgotPassword(request, new HttpCallback<VoidResponse>() {
            @Override
            public void onSuccess(VoidResponse response) {
                runOnUiThread(() -> {
                    toast("Verification code resent.");
                    if (countDownTimer != null) {
                        countDownTimer.cancel(); // Stop current timer
                    }
                    startCountdown(); // Restart timer
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> toast("Failed sending code: " + message));
            }
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
    }
    private void handleSubmit() {
        String email = getEmail();
        String code = getCode();

        if (email == null || code == null) return;

        PasswordManagementService.VerifyCodeRequest request = new PasswordManagementService.VerifyCodeRequest();
        request.code = code;
        request.email = email;

        PasswordManagementService.verifyCode(request, new HttpCallback<VoidResponse>() {
            @Override
            public void onSuccess(VoidResponse response) {
                runOnUiThread(() -> {
                    toast("Code verification success.");

                    passwordResetManager.setCode(code);

                    Intent intent=new Intent(FrgtPassEmailVerification.this, ResetPass.class);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> toast("Failed verifying code: " + message));
            }
        });
    }
    private String getEmail() {
        if (email == null) toast("Email could not be found. Please try again later.");
        return email;
    }
    private String getCode() {
        StringBuilder sb = new StringBuilder();

        for (EditText input : inputs) {
            String digit = input.getText().toString();
            if (digit.isBlank()) {
                toast("Please input a valid 6-digit code.");
                return null;
            }
            sb.append(input.getText().toString());
        }

        return sb.toString();
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
