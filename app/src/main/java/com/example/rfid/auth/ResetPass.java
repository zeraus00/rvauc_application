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

public class ResetPass extends AppCompatActivity {
    private EditText passwordView;
    private EditText confirmPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        passwordView = findViewById(R.id.editTextTextPassword2);
        confirmPasswordView = findViewById(R.id.editTextTextPassword3);

        Button submit_Btn = findViewById(R.id.submitBtn);
        submit_Btn.setOnClickListener(v -> handleSubmit());

        Button Cancel_btn = findViewById(R.id.cancelBtn);
        Cancel_btn.setOnClickListener(v -> {
            PasswordResetManager.getInstance().setEmail(null);
            PasswordResetManager.getInstance().setCode(null);
            startActivity(new Intent(ResetPass.this, LoginActivity.class));
            finish();
        });

        ImageButton backBtn = findViewById(R.id.imageButton);
        backBtn.setOnClickListener(v -> {
           PasswordResetManager.getInstance().setCode(null);
           startActivity(new Intent(ResetPass.this, FrgtPassEmailVerification.class));
           finish();
        });
    }
    private void handleSubmit() {
        String code = PasswordResetManager.getInstance().getCode();
        String password = passwordView.getText().toString();
        String confirmPassword = confirmPasswordView.getText().toString();

        if (password.isBlank() || confirmPassword.isBlank()) {
            toast("Both fields must be filled out.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            toast("Passwords do not match.");
            return;
        }

        resetPassword(code, password, confirmPassword);
    }

    private void resetPassword(String code, String password, String confirmPassword) {
        PasswordManagementService.ResetPasswordRequest request = new PasswordManagementService.ResetPasswordRequest() {};
        request.code = code;
        request.password = password;
        request.confirmPassword = confirmPassword;

        PasswordManagementService.resetPassword(request, new HttpCallback<VoidResponse>() {
            @Override
            public void onSuccess(VoidResponse response) {
                runOnUiThread(() -> {
                    PasswordResetManager.getInstance().setEmail(null);
                    PasswordResetManager.getInstance().setCode(null);

                    toast("Successfully changed password.");

                    startActivity(new Intent(ResetPass.this, PassUpdate.class));
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> toast("Failed resetting password: " + message));
            }
        });
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}