package com.example.rfid.services;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final TokenProvider tokenProvider;

    public AuthInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }


    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        var request = chain.request();

        if (isMarkedForInjection(request)) {
            var token = this.tokenProvider.call();

            if (token != null && !token.isBlank()) {
                var newRequest = request.newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }
        }

        return chain.proceed(request);
    }

    public interface TokenProvider {
        String call();
    }

    private boolean isMarkedForInjection(Request request) {
        return request.header("X-Inject-Auth") != null;
    }
}
