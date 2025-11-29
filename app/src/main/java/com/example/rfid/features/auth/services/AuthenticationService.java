package com.example.rfid.features.auth.services;

import static com.example.rfid.services.RvaucMsService.rvaucMsCallback;

import com.example.rfid.dto.ApiResponse;
import com.example.rfid.dto.VoidResponse;
import com.example.rfid.interfaces.HttpCallback;
import com.example.rfid.services.RvaucMsService;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class AuthenticationService {
    private static AuthenticationClient authenticationClient;
    public static void requestSignInCode(SignInCodeRequest request, HttpCallback<VoidResponse> callback) {
        var auth = getAuthenticationClient();

        auth.requestSignInCode(request).enqueue(rvaucMsCallback(callback));

    }
    public static void verifyCode(VerifyCodeRequest request, HttpCallback<TokensResponse> callback) {
        var auth = getAuthenticationClient();

        auth.verifyCode(request).enqueue(rvaucMsCallback(callback));
    }

    public static void signOut(SignOutRequest request, HttpCallback<VoidResponse> callback) {
        var auth = getAuthenticationClient();

        auth.signOut(request).enqueue(rvaucMsCallback(callback));
    }
    private static AuthenticationClient getAuthenticationClient() {
        if (authenticationClient == null) authenticationClient = RvaucMsService.createService(AuthenticationClient.class);
        return authenticationClient;
    }

    interface AuthenticationClient {
        @POST("/auth/session-management/sign-in")
        Call<VoidResponse> requestSignInCode(@Body SignInCodeRequest request);

        @POST("/auth/session-management/verify-code")
        Call<TokensResponse> verifyCode(@Body VerifyCodeRequest request);

        @POST("/auth/session-management/sign-out")
        Call<VoidResponse> signOut(@Body SignOutRequest signOutRequest);
    }
    public static class SignInCodeRequest {
        public String identifier;
        public String password;
        public boolean isPersistentAuth = false;
        public SignInCodeRequest(){}
    }
    public static class VerifyCodeRequest {
        public String email;
        public String code;
        public boolean isPersistentAuth = false;
        public VerifyCodeRequest() {}
    }
    public static class SignOutRequest {
        public String refreshToken;
        public SignOutRequest(){}
    }
    public static class TokensResponse extends ApiResponse<Tokens> {}
    public static class Tokens {
        public String accessToken;
        public String refreshToken;
        public Tokens(){}
    }
}
