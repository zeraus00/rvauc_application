package com.example.rfid.auth;

import static com.example.rfid.utils.JsonParser.fromJson;
import static com.example.rfid.utils.JsonParser.toJson;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rfid.R;
import com.example.rfid.features.auth.services.AuthenticationService;
import com.example.rfid.dto.ApiResponse;
import com.example.rfid.interfaces.HttpCallback;
import com.fasterxml.jackson.core.type.TypeReference;


public class LoginActivity extends AppCompatActivity {
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

        Button login_btn = findViewById(R.id.button);
        EditText emailView = findViewById(R.id.editTextTextEmailAddress2);
        EditText passwordView = findViewById(R.id.editTextTextPassword);
        CheckBox rememberMeView = findViewById(R.id.checkBox2);

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

        TextView forgot=findViewById(R.id.forgot);

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, FrgtPassword.class);
                startActivity(intent);
                finish();
            }
        });

    }


    void loginUser(String email, String password, boolean rememberMe) {
        var loginRequest = new AuthenticationService.LoginRequest(){};
        loginRequest.identifier = email;
        loginRequest.password = password;
        loginRequest.isPersistentAuth = rememberMe;

        String json = toJson(loginRequest);

        Log.d(authTag, "Requesting code...");
        AuthenticationService.login(json, new HttpCallback() {
            @Override
            public void onSuccess(String json) {
                runOnUiThread(() -> {
                    var res = fromJson(json, new TypeReference<ApiResponse<Void>>() {});

                    SharedPreferences prefs = getSharedPreferences("RvaucMs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    if (res.success) {
                        Log.i(authTag, "Success requesting code.");
                        editor.putString("email", email);
                        editor.putBoolean("rememberMe", rememberMe);
                        editor.apply();
                    } else {
                        Log.i(authTag, "Failed requesting code.");
                        editor.clear().apply();
                        toastFail(res.message);
                        return;
                    }

                    Intent intent = new Intent(LoginActivity.this, LoginEmailVerification.class);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Log.e(authTag, "Sign in code request failed.", e);
                    toastFail("Authentication failed: " + e.getMessage());
                });
            }
        });
    }

    private void toastFail(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}