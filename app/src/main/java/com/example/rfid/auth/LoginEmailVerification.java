package com.example.rfid.auth;

import static com.example.rfid.utils.JsonParser.fromJson;
import static com.example.rfid.utils.JsonParser.toJson;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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
import com.example.rfid.dto.ApiResponse;
import com.example.rfid.interfaces.HttpCallback;
import com.fasterxml.jackson.core.type.TypeReference;

public class LoginEmailVerification extends AppCompatActivity {
    private final String authTag = "Authentication";
    private TextView textCountdown;
    private CountDownTimer countDownTimer;

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
        var resendText = findViewById(R.id.txtResendCode); // your “Resend” text
        ImageButton backBtn = findViewById(R.id.backButton);
        var loginBtn = findViewById(R.id.btnVerifying1);
        EditText[] inputs = {
                findViewById(R.id.etInputBox1),
                findViewById(R.id.etInputBox2),
                findViewById(R.id.etInputBox3),
                findViewById(R.id.etInputBox4),
                findViewById(R.id.etInputBox5),
                findViewById(R.id.etInputBox6)
        };

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
            SharedPreferences prefs = getSharedPreferences("RvaucMs", MODE_PRIVATE);
            String email = prefs.getString("email", null);
            boolean rememberMe = prefs.getBoolean("rememberMe", false);

            if (email == null || email.isBlank()) {
                toastFail("Please retry logging-in");
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
    void verifyCode(String email, String code, boolean rememberMe) {
        var verifyRequest = new AuthenticationService.VerifyCodeRequest() {};
        verifyRequest.email = email;
        verifyRequest.code = code;
        verifyRequest.isPersistentAuth = rememberMe;

        String json = toJson(verifyRequest);

        AuthenticationService.verifyCode(json, new HttpCallback() {
            @Override
            public void onSuccess(String json) {
                runOnUiThread(() -> {
                    var res = fromJson(json, new TypeReference<ApiResponse<AuthenticationService.Tokens>>() {});

                    if (res.success) {
                        SharedPreferences prefs = getSharedPreferences("RvaucMs", MODE_PRIVATE);
                        var editor = prefs.edit();
                        editor.putString("refreshToken", res.result.refreshToken);
                        editor.apply();

                        SessionManager.getInstance().setAccessToken(res.result.accessToken);
                    }
                    else {
                        toastFail(res.message);
                        return;
                    }
                    startActivity(new Intent(LoginEmailVerification.this, EmailVerifiedSuccessful.class));
                    finish();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(()-> toastFail("Failed verifying code: " + e.getMessage()));
            }
        });
    }
    private void toastFail(String message) {
        Toast.makeText(LoginEmailVerification.this, message, Toast.LENGTH_SHORT).show();
    }
}
