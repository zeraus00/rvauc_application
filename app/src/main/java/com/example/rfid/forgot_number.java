package com.example.rfid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class forgot_number extends AppCompatActivity {
    private ImageButton backButton;
    private Button btn_another;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_number);

        // Handle edge-to-edge layout padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        backButton = findViewById(R.id.backButton);
        btn_another = findViewById(R.id.btn_another);

        // Back button → go to MainActivity
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(forgot_number.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Another button → go to forgot activity
        btn_another.setOnClickListener(v -> {
            Intent intent = new Intent(forgot_number.this, forgot.class);
            startActivity(intent);
            finish();
        });
    }
}
