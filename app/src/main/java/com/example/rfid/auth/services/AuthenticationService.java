package com.example.rfid.auth.services;

import com.example.rfid.interfaces.HttpCallback;
import com.example.rfid.services.RequestService;

public class AuthenticationService {
    public static void login(String jsonReq, HttpCallback callback) {
        String url = "http://10.0.2.2:2620/auth/session-management/sign-in";
        new RequestService().post(url, jsonReq, callback);
    }

    public static void verifyCode(String jsonReq, HttpCallback callback) {
        String url = "http://10.0.2.2:2620/auth/session-management/verify-code";
        new RequestService().post(url, jsonReq, callback);
    }


    public static class LoginRequest {
        public String identifier;
        public String password;
        public boolean isPersistentAuth = false;
        public LoginRequest(){}
    }
    public static class VerifyCodeRequest {
        public String email;
        public String code;
        public boolean isPersistentAuth = false;
        public VerifyCodeRequest() {}
    }
    public static class Tokens {
        public String accessToken;
        public String refreshToken;
        public Tokens(){}
    }
}
