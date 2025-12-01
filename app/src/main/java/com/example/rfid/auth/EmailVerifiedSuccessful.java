package com.example.rfid.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rfid.R;
import com.example.rfid.main_app.homePage;

public class EmailVerifiedSuccessful extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_email_verified_successful);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imgCheck = findViewById(R.id.imgCheck);

        imgCheck.setScaleX(1f);
        imgCheck.setScaleY(1f);

        imgCheck.animate()
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setDuration(300)
                .withEndAction(() -> {

        imgCheck.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(300)
                .withEndAction(() -> {

        imgCheck.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(250);
                });
        });


        Button btn_to_login = findViewById(R.id.btnVerifying);

        btn_to_login.setOnClickListener(v -> {
            startActivity(new Intent(EmailVerifiedSuccessful.this, homePage.class));
            finish();
        });
    }
}