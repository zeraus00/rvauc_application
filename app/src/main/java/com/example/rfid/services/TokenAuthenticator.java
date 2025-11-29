package com.example.rfid.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rfid.features.auth.services.AuthenticationService;
import com.example.rfid.features.auth.services.SessionManager;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {
    private final SessionManager sessionManager;

    public TokenAuthenticator() {
        sessionManager = SessionManager.getInstance();
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
        synchronized (this) {
            if (!isMarkedForInjection(response.request())) return null;

            var currentAccessToken = sessionManager.getAccessToken();
            var refreshToken = sessionManager.getRefreshToken();

            var header = response.request().header("Authorization");
            var headerToken = header != null ? header.split(" ")[1] : null;

            if (!currentAccessToken.equals(headerToken)) {
                return buildNewRequest(response, currentAccessToken);
            }


            var refreshRequest = new AuthenticationService.RefreshTokensRequest();
            refreshRequest.refreshToken = refreshToken;
            var refreshResponse = AuthenticationService.refreshTokens(refreshRequest);
            if (!refreshResponse.isSuccessful()) return null;

            var resBody = refreshResponse.body();

            if(resBody == null) return null;
            if(!resBody.success) return null;

            sessionManager.setRefreshToken(resBody.result.refreshToken);
            sessionManager.setAccessToken(resBody.result.accessToken);

            return buildNewRequest(response, resBody.result.accessToken);

        }
    }
    private boolean isMarkedForInjection(Request request) {
        return request.header("X-Inject-Auth") != null;
    }
    private Request buildNewRequest(Response response, String token) {
        return response.request().newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
    }

}
