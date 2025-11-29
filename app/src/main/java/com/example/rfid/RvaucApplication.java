package com.example.rfid;

import android.app.Application;

import com.example.rfid.features.auth.services.SessionManager;
import com.example.rfid.services.AuthInterceptor;
import com.example.rfid.services.RvaucMsService;
import com.example.rfid.services.TokenAuthenticator;

import okhttp3.Interceptor;

public class RvaucApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SessionManager.init(this);
        RvaucMsService.init(
                new Interceptor[] {
                    new AuthInterceptor(() -> SessionManager.getInstance().getAccessToken())
                },
                new TokenAuthenticator()
        );
    }
}
