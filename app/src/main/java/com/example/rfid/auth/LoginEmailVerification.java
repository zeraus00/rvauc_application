package com.example.rfid.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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
    private TextView userEmailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_e_verification);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = SessionManager.getInstance();
        email = sessionManager.getEmail();

        userEmailView = findViewById(R.id.userEmail);
        textCountdown = findViewById(R.id.txtCountdown);

        if (email != null) {
            userEmailView.setText(email);
        } else {
            userEmailView.setText(getString(R.string.Email));
        }

        TextView resendText = findViewById(R.id.txtResendCode);
        ImageButton backBtn = findViewById(R.id.btnBack);
        Button loginBtn = findViewById(R.id.btnVerifying1);

        EditText[] inputs = {
                findViewById(R.id.etInputBox1),
                findViewById(R.id.etInputBox2),
                findViewById(R.id.etInputBox3),
                findViewById(R.id.etInputBox4),
                findViewById(R.id.etInputBox5),
                findViewById(R.id.etInputBox6)
        };

        for (int i = 0; i < inputs.length; i++) {
            final int index = i;

            inputs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < inputs.length - 1){
                        inputs[index + 1].requestFocus();
                    }
                    else if (s.length() == 0 && index > 0){
                        inputs[index - 1].requestFocus();
                        inputs[index - 1].setSelection(inputs[index - 1].getText().length());
                    }
                }
            });
        }

        backBtn.setOnClickListener(v -> {

            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            startActivity(new Intent(LoginEmailVerification.this, LoginActivity.class));
            finish();
        });

        startCountdown();


        resendText.setOnClickListener(v -> {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            startCountdown();
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

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void startCountdown() {
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
            }
        }.start();
    }
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