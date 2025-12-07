package com.example.rfid.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View; // Import View for the listener
import android.widget.Button; // Changed var to Button
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
import com.example.rfid.features.auth.services.AuthenticationService;
import com.example.rfid.features.auth.services.SessionManager;
import com.example.rfid.interfaces.HttpCallback;

public class LoginEmailVerification extends AppCompatActivity {
    private SessionManager sessionManager;
    private String email;
    private final String authTag = "Authentication";
    private TextView textCountdown;
    private CountDownTimer countDownTimer;
    private TextView userEmailView; // Declaring globally for easy access

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Ensure this layout ID matches the XML file name used in R.layout.
        setContentView(R.layout.activity_e_verification);

        // Handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = SessionManager.getInstance();
        email = sessionManager.getEmail();

        // FIX: Correctly reference the TextView using the XML ID 'userEmail'
        userEmailView = findViewById(R.id.userEmail);
        textCountdown = findViewById(R.id.txtCountdown);

        // Use the actual email retrieved from the session manager
        if (email != null) {
            userEmailView.setText(email);
        } else {
            userEmailView.setText(getString(R.string.Email));
        }

        TextView resendText = findViewById(R.id.txtResendCode);
        ImageButton backBtn = findViewById(R.id.btnBack);
        Button loginBtn = findViewById(R.id.btnVerifying1); // Use Button instead of var

        EditText[] inputs = {
                findViewById(R.id.etInputBox1),
                findViewById(R.id.etInputBox2),
                findViewById(R.id.etInputBox3),
                findViewById(R.id.etInputBox4),
                findViewById(R.id.etInputBox5),
                findViewById(R.id.etInputBox6)
        };

        // --- Listeners ---

        backBtn.setOnClickListener(v -> {
            // Stop the countdown before leaving the activity
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
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
            // Optionally, call API to resend the code here
            Toast.makeText(this, "Verification code resent!", Toast.LENGTH_SHORT).show();
        });

        loginBtn.setOnClickListener(v -> {
            boolean rememberMe = sessionManager.getRememberMe();

            if (email == null || email.isBlank()) {
                toastFail("Session error. Please retry logging-in.");
                return;
            }

            Log.i(authTag, "Retrieving code...");

            StringBuilder code = new StringBuilder();

            for (EditText input: inputs) {
                String digit = input.getText().toString();
                if (digit.isBlank()) {
                    toastFail("Please input a complete 6-digit code.");
                    return;
                }
                code.append(digit);
            }

            verifyCode(email, code.toString(), rememberMe);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure the timer is cancelled when the activity is destroyed to prevent memory leaks
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void startCountdown() {
        // Cancel existing timer before starting a new one
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Start 60-second timer
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                textCountdown.setText("00:" + String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                textCountdown.setText("00:00");
                // Optional: Disable the verification button here if the code expires
            }
        }.start();
    }

    // [The rest of the verifyCode and toastFail methods remain the same]
    void verifyCode(String email, String code, boolean rememberMe) {
        var verifyCodeRequest = new AuthenticationService.VerifyCodeRequest() {};
        verifyCodeRequest.email = email;
        verifyCodeRequest.code = code;
        verifyCodeRequest.isPersistentAuth = rememberMe;

        AuthenticationService.verifyCode(verifyCodeRequest, new HttpCallback<AuthenticationService.TokensResponse>() {
            @Override
            public void onSuccess(AuthenticationService.TokensResponse response) {
                runOnUiThread(() -> {
                    if (response.success) {
                        sessionManager.setRefreshToken(response.result.refreshToken);
                        sessionManager.setAccessToken(response.result.accessToken);
                    }
                    else {
                        toastFail(response.message);
                        return;
                    }
                    // Stop timer on successful verification
                    if (countDownTimer != null) countDownTimer.cancel();
                    startActivity(new Intent(LoginEmailVerification.this, EmailVerifiedSuccessful.class));
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(()-> toastFail("Failed verifying code: " + message));
            }
        });
    }
    private void toastFail(String message) {
        Toast.makeText(LoginEmailVerification.this, message, Toast.LENGTH_SHORT).show();
    }
}