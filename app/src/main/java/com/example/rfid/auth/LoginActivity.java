package com.example.rfid.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rfid.R;
import com.example.rfid.dto.VoidResponse;
import com.example.rfid.features.auth.services.AuthenticationService;
import com.example.rfid.features.auth.services.SessionManager;
import com.example.rfid.interfaces.HttpCallback;
import com.example.rfid.main_app.homePage;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private final String authTag = "Authentication";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        var token = SessionManager.getInstance().getRefreshToken();

        if (token != null) {
            startActivity(new Intent(this, homePage.class));
        }

        sessionManager = SessionManager.getInstance();

        Button login_btn = findViewById(R.id.button);
        EditText emailView = findViewById(R.id.editTextTextEmailAddress2);
        EditText passwordView = findViewById(R.id.editTextTextPassword);
        CheckBox rememberMeView = findViewById(R.id.checkBox2);
        ImageView ivTogglePassword = findViewById(R.id.ivTogglePassword);

        ivTogglePassword.setOnClickListener(v -> {
            if (passwordView.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                passwordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.view);
            } else {
                passwordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.hide);
            }

            passwordView.setSelection(passwordView.getText().length());
        });

        login_btn.setOnClickListener(v -> {
            Log.i(authTag, "Attempting to request sign in code.");

            String email = emailView.getText().toString();
            String password = passwordView.getText().toString();
            boolean rememberMe = rememberMeView.isActivated();

            if (email.isBlank() || password.isBlank()) {
                Log.i(authTag, "Failed requesting code. A field was missing.");
                toastFail("Email and password cannot be empty");
                return;
            }

            loginUser(email, password, rememberMe);
        });

        TextView forgot = findViewById(R.id.forgot);
        forgot.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, FrgtPassword.class);
            startActivity(intent);
            finish();
        });

    }

    void loginUser(String email, String password, boolean rememberMe) {
        var signInCodeRequest = new AuthenticationService.SignInCodeRequest(){};
        signInCodeRequest.identifier = email;
        signInCodeRequest.password = password;
        signInCodeRequest.isPersistentAuth = rememberMe;

        Log.d(authTag, "Requesting code...");

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FCM", "Device token: " + task.getResult());
                signInCodeRequest.deviceToken = task.getResult();
            }
            else {
                Log.e("FCM", "Token retrieval failed: " + task.getException());
            }

            AuthenticationService.requestSignInCode(signInCodeRequest, new HttpCallback<VoidResponse>() {
                @Override
                public void onSuccess(VoidResponse response) {
                    runOnUiThread(() -> {
                        if (response.success) {
                            Log.i(authTag, "Success requesting code.");
                            sessionManager.setEmail(email);
                            sessionManager.setRememberMe(rememberMe);
                        } else {
                            Log.i(authTag, "Failed requesting code.");
                            sessionManager.clear();
                            toastFail(response.message);
                            return;
                        }

                        Intent intent = new Intent(LoginActivity.this, LoginEmailVerification.class);
                        startActivity(intent);
                        finish();
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> {
                        String failMsg = "Authentication failed: " + message;
                        Log.e(authTag, failMsg);
                        toastFail(failMsg);
                    });
                }
            });
        });
    }

    private void toastFail(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
