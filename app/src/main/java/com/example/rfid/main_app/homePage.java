package com.example.rfid.main_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.rfid.R;
import com.example.rfid.adapters.CarouselAdapter;
import com.example.rfid.auth.LoginActivity;
import com.example.rfid.dto.VoidResponse;
import com.example.rfid.features.auth.services.AuthenticationService;
import com.example.rfid.features.auth.services.SessionManager;
import com.example.rfid.interfaces.HttpCallback;

public class homePage extends AppCompatActivity {

    private LinearLayout statsBtn, classBtn, homeBtn, notifBtn, policyBtn;

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


        SessionManager sessionManager = SessionManager.getInstance();
        TextView welcomeView = findViewById(R.id.txtWelcome);
        var payload = sessionManager.getPayload();
        String username = payload == null ? "pogi" : payload.getUsername();
        welcomeView.setText("Welcome, " + username);


        ImageButton logoutBtn = findViewById(R.id.imagebtn_logout);
        logoutBtn.setOnClickListener(v -> logoutUser());

        ImageView profileImg = findViewById(R.id.imgProfile);
        profileImg.setOnClickListener(v -> {
            openProfileFragment();
        });


        statsBtn = findViewById(R.id.statslayout);
        classBtn = findViewById(R.id.classlayout);
        homeBtn = findViewById(R.id.homelayout);
        notifBtn = findViewById(R.id.notiflayout);
        policyBtn = findViewById(R.id.policylayout);

        statsBtn.setOnClickListener(v -> {
            loadFragment(new StatusFragment());
            setActiveTab(statsBtn);
        });

        classBtn.setOnClickListener(v -> {
            loadFragment(new ClassFragment());
            setActiveTab(classBtn);
        });

        homeBtn.setOnClickListener(v -> {
            clearFragment();
            setActiveTab(homeBtn);
        });

        notifBtn.setOnClickListener(v -> {
            loadFragment(new NotificationFragment());
            setActiveTab(notifBtn);
        });

        policyBtn.setOnClickListener(v -> {
            loadFragment(new PolicyFragment());
            setActiveTab(policyBtn);
        });

        ViewPager2 viewPager = findViewById(R.id.carouselSwitcher);
        int[] images = {
                R.drawable.type_a,
                R.drawable.buffalo,
                R.drawable.department_shirt,
                R.drawable.pe_uniform
        };
        CarouselAdapter adapter = new CarouselAdapter(images);
        viewPager.setAdapter(adapter);

        Handler handler = new Handler();
        Runnable autoSlide = new Runnable() {
            @Override
            public void run() {
                int next = viewPager.getCurrentItem() + 1;
                if (next >= images.length) next = 0;
                viewPager.setCurrentItem(next, true);
                handler.postDelayed(this, 4000);
            }
        };
        handler.postDelayed(autoSlide, 4000);
    }

    private void openProfileFragment() {
        loadFragment(new profileFragment());
        clearNavHighlight();
    }

    private void setActiveTab(LinearLayout active) {
        resetNavItem(statsBtn);
        resetNavItem(classBtn);
        resetNavItem(homeBtn);
        resetNavItem(notifBtn);
        resetNavItem(policyBtn);

        if (active != null) highlightNavItem(active);
    }

    private void clearNavHighlight() {
        resetNavItem(statsBtn);
        resetNavItem(classBtn);
        resetNavItem(homeBtn);
        resetNavItem(notifBtn);
        resetNavItem(policyBtn);
    }
    private void resetNavItem(LinearLayout item) {
        if (item == null) return;
        try {
            if (item.getChildCount() >= 2) {
                ImageView icon = (ImageView) item.getChildAt(0);
                TextView label = (TextView) item.getChildAt(1);
                icon.setColorFilter(getColor(R.color.green));
                label.setTextColor(getColor(R.color.green));
                item.setBackgroundColor(getColor(android.R.color.transparent));
            }
        } catch (Exception ignored) {
        }
    }

    // safe highlight
    private void highlightNavItem(LinearLayout item) {
        if (item == null) return;
        try {
            if (item.getChildCount() >= 2) {
                ImageView icon = (ImageView) item.getChildAt(0);
                TextView label = (TextView) item.getChildAt(1);
                icon.setColorFilter(getColor(R.color.white));
                label.setTextColor(getColor(R.color.white));
                item.setBackgroundColor(getColor(R.color.green));
            }
        } catch (Exception ignored) { }
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void clearFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new Fragment())
                .commit();
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
}
