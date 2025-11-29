package com.example.rfid.main_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.rfid.R;
import com.example.rfid.auth.LoginActivity;
import com.example.rfid.dto.VoidResponse;
import com.example.rfid.features.auth.services.AuthenticationService;
import com.example.rfid.features.auth.services.SessionManager;
import com.example.rfid.interfaces.HttpCallback;

public class homePage extends AppCompatActivity {
    ImageButton statsBtn, classBtn, homeBtn, notifBtn, policyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton logoutBtn = findViewById(R.id.imagebtn_logout);

        logoutBtn.setOnClickListener(v -> logoutUser());

        statsBtn = findViewById(R.id.stats);
        classBtn = findViewById(R.id.btn_class);
        homeBtn = findViewById(R.id.btn_home);
        notifBtn = findViewById(R.id.btn_notif);
        policyBtn = findViewById(R.id.btn_policy);

        statsBtn.setOnClickListener(v -> loadFragment(new StatusFragment()));
        classBtn.setOnClickListener(v -> loadFragment(new ClassFragment()));
        notifBtn.setOnClickListener(v -> loadFragment(new NotificationFragment()));
        policyBtn.setOnClickListener(v -> loadFragment(new PolicyFragment()));

        homeBtn.setOnClickListener(v -> clearFragment());

        ImageSwitcher carousel = findViewById(R.id.carouselSwitcher);

        if (carousel != null) {

            // Factory for ImageSwitcher
            carousel.setFactory(() -> {
                ImageView img = new ImageView(homePage.this);
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                img.setLayoutParams(new ImageSwitcher.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));
                return img;
            });


            int[] images = {
                    R.drawable.type_a,
                    R.drawable.buffalo,
                    R.drawable.department_shirt,
                    R.drawable.pe_uniform
            };

            final int[] index = {0};


            carousel.setImageResource(images[index[0]]);

            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    index[0]++;
                    if (index[0] >= images.length) index[0] = 0;

                    carousel.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                    carousel.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));

                    carousel.setImageResource(images[index[0]]);
                    handler.postDelayed(this, 3000);
                }
            };

            handler.postDelayed(runnable, 3000);
        }

    }

    public void logoutUser() {
        var logOutRequest = new AuthenticationService.SignOutRequest() {};
        logOutRequest.refreshToken = SessionManager.getInstance().getRefreshToken();

        AuthenticationService.signOut(logOutRequest, new HttpCallback<VoidResponse>() {
            @Override
            public void onSuccess(VoidResponse response) {
                runOnUiThread(() -> {
                    if (response.success) SessionManager.getInstance().clear();

                    startActivity(new Intent(homePage.this, LoginActivity.class));
                    finish();
                });
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    public void clearFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new Fragment())
                .commit();
    }

}
