package com.example.rfid.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FrgtPassword extends AppCompatActivity {
    private ImageButton backButton;
    private EditText emailView;

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

        emailView = findViewById(R.id.editTextTextEmailAddress);

        backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> {
            PasswordResetManager.getInstance().setEmail(null);
            PasswordResetManager.getInstance().setCode(null);
            Intent intent = new Intent(FrgtPassword.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        Button verifying_btn = findViewById(R.id.button3);

        verifying_btn.setOnClickListener(v -> handleRequest());
    }

    private void handleRequest() {
        String email = emailView.getText().toString();

        if (email.isBlank() || !isValidEmail(email)) {
            toast("Invalid email!");
            return;
        }

        requestCode(email);
    }

    private void requestCode(String email) {
        PasswordManagementService.ForgotPasswordRequest request = new PasswordManagementService.ForgotPasswordRequest();
        request.email = email;

        PasswordManagementService.forgotPassword(request, new HttpCallback<VoidResponse>() {
            @Override
            public void onSuccess(VoidResponse response) {
                runOnUiThread(() -> {
                    toast("Password request code has been sent to your email.");

                    PasswordResetManager.getInstance().setEmail(email);

                    Intent intent = new Intent(FrgtPassword.this, FrgtPassEmailVerification.class);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> toast("Failed sending code: " + message));
            }
        });
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
        Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
