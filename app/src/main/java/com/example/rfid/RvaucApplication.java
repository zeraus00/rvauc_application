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

        session.setEmail("dev@local");
        session.setAccessToken("dev-token");
        session.setRememberMe(true);

        //  optional mock payload
    }
}
