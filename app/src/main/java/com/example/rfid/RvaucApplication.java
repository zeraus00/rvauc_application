package com.example.rfid;

import android.app.Application;
import android.util.Log;

import com.example.rfid.features.auth.services.SessionManager;
import com.example.rfid.services.AuthInterceptor;
import com.example.rfid.services.RvaucMsService;
import com.example.rfid.services.TokenAuthenticator;
import com.google.firebase.FirebaseApp;

import okhttp3.Interceptor;

public class RvaucApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        SessionManager.init(this);
        RvaucMsService.init(
                new Interceptor[] {
                    new AuthInterceptor(() -> SessionManager.getInstance().getAccessToken())
                },
                new TokenAuthenticator()
        );

        if (BuildConfig.DEBUG && BuildConfig.USE_MOCK_AUTH) enableDevSession();
    }
    private void enableDevSession() {
        Log.d("DEV_SESSION_ENABLED", "Developer session enabled. Authentication is mocked.");
        SessionManager session = SessionManager.getInstance();

        session.setAccessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NywiZW1haWwiOiJsZWUuYWdhdG9uQGdtYWlsLmNvbSIsInVzZXJuYW1lIjoiTGVlQTciLCJyb2xlIjoic3R1ZGVudCIsInN1cm5hbWUiOiJBZ2F0b24iLCJmaXJzdE5hbWUiOiJMZWUgQXJjaGVsYXVzIiwibWlkZGxlTmFtZSI6IiIsImdlbmRlciI6Im1hbGUiLCJjb250YWN0TnVtYmVyIjoiMDkxNzEyMzQ1MDciLCJkZXBhcnRtZW50IjoiRGVwYXJ0bWVudCBPZiBDb21wdXRlciBTY2llbmNlIiwic3R1ZGVudE51bWJlciI6IjEwMS0wMDAxIiwieWVhckxldmVsIjozLCJibG9jayI6IkEiLCJpYXQiOjE3NzcyMDIzNzcsImV4cCI6MjA5Mjc3ODM3N30.mPEquFTY03eQS205YhcLvKSvmRXO9_rTwYvpUXQU19k");
        session.setRememberMe(true);

        //  optional mock payload
    }
}
