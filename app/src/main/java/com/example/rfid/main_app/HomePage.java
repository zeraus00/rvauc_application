package com.example.rfid.main_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.PopupMenu;
import android.view.MenuItem; // Make sure this is imported

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

public class HomePage extends AppCompatActivity {

    private LinearLayout statsBtn, classBtn, homeBtn, notifBtn, policyBtn;
    private final Runnable logoutListener = () -> {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        SessionManager.getInstance().addLogoutListener(logoutListener);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        SessionManager sessionManager = SessionManager.getInstance();
        TextView welcomeView = findViewById(R.id.txtWelcome);
        var payload = sessionManager.getPayload();
        String username = payload == null ? "pogi" : payload.getSurname() + ".";
        welcomeView.setText("Welcome, " + username);



        ImageView profileImg = findViewById(R.id.imgProfile);
        profileImg.setOnClickListener(this::showPopupMenu);


        statsBtn = findViewById(R.id.statslayout);
        classBtn = findViewById(R.id.classlayout);
        homeBtn = findViewById(R.id.homelayout);
        notifBtn = findViewById(R.id.notiflayout);
        policyBtn = findViewById(R.id.policylayout);

        statsBtn.setOnClickListener(v -> {
            loadFragment(new StatusFragment());
            hideHeader();
            setActiveTab(statsBtn);
        });

        classBtn.setOnClickListener(v -> {
            loadFragment(new ClassFragment());
            hideHeader();
            setActiveTab(classBtn);
        });

        homeBtn.setOnClickListener(v -> {
            clearFragment();
            showHeader();
            setActiveTab(homeBtn);
        });

        notifBtn.setOnClickListener(v -> {
            loadFragment(new NotificationFragment());
            hideHeader();
            setActiveTab(notifBtn);
        });

        policyBtn.setOnClickListener(v -> {
            loadFragment(new PolicyFragment());
            hideHeader();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SessionManager.getInstance().removeLogoutListener(logoutListener);
    }

    private void showHeader() {
        View header = findViewById(R.id.headerContainer);
        ImageView luLogo = findViewById(R.id.luLogo);
        TextView luAxis = findViewById(R.id.luAxis);
        TextView txtWelcome = findViewById(R.id.txtWelcome);

        luLogo.setVisibility(View.VISIBLE);
        luAxis.setVisibility(View.VISIBLE);
        txtWelcome.setVisibility(View.VISIBLE);
    }

    private void hideHeader() {
        View header = findViewById(R.id.headerContainer);
        ImageView luLogo = findViewById(R.id.luLogo);
        TextView luAxis = findViewById(R.id.luAxis);
        TextView txtWelcome = findViewById(R.id.txtWelcome);

        luLogo.setVisibility(View.GONE);
        luAxis.setVisibility(View.GONE);
        txtWelcome.setVisibility(View.GONE);
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);

        popupMenu.getMenuInflater().inflate(R.menu.logout_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.menu_logout_action) {
                    logoutUser();
                    return true;
                } else if (id == R.id.menu_profile) {
                    openProfileFragment();
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void openProfileFragment() {
        loadFragment(new ProfileFragment());
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
                    startActivity(new Intent(HomePage.this, LoginActivity.class));
                    finish();
                });
            }

            @Override
            public void onError(String message) {
            }
        });
    }
}