package com.example.rfid.main_app;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.rfid.R;
import com.example.rfid.adapters.CarouselAdapter;
import com.example.rfid.features.auth.services.SessionManager;

public class HomeFragment extends Fragment {

    private Handler handler = new Handler();
    private Runnable autoSlide;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ViewPager2 viewPager = view.findViewById(R.id.carouselSwitcher);

        int[] images = {
                R.drawable.type_a,
                R.drawable.buffalo,
                R.drawable.department_shirt,
                R.drawable.pe_uniform
        };

        CarouselAdapter adapter = new CarouselAdapter(images);
        viewPager.setAdapter(adapter);

        autoSlide = new Runnable() {
            @Override
            public void run() {
                int next = viewPager.getCurrentItem() + 1;
                if (next >= images.length) next = 0;
                viewPager.setCurrentItem(next, true);
                handler.postDelayed(this, 4000);
            }
        };

        handler.postDelayed(autoSlide, 4000);

        TextView welcomeView = view.findViewById(R.id.txtWelcome);

        SessionManager sessionManager = SessionManager.getInstance();
        var payload = sessionManager.getPayload();
        String username = payload == null ? "pogi" : payload.getSurname() + ".";

        welcomeView.setText("Welcome, " + username);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (autoSlide != null) {
            handler.removeCallbacks(autoSlide);
        }
    }
}