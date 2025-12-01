package com.example.rfid.features.auth.services;

import static com.example.rfid.services.RvaucMsService.rvaucMsCallback;

import com.example.rfid.dto.VoidResponse;
import com.example.rfid.interfaces.HttpCallback;
import com.example.rfid.services.RvaucMsService;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class PasswordManagementService {
    private static Client client;
    public static void forgotPassword(ForgotPasswordRequest request, HttpCallback<VoidResponse> callback) {
        getClient().forgotPassword(request).enqueue(rvaucMsCallback(callback));
    }
    public static void verifyCode(VerifyCodeRequest request, HttpCallback<VoidResponse> callback) {
        getClient().verifyCode(request).enqueue(rvaucMsCallback(callback));
    }
    public static void resetPassword(ResetPasswordRequest request, HttpCallback<VoidResponse> callback) {
        getClient().resetPassword(request).enqueue(rvaucMsCallback(callback));
    }
    private static Client getClient() {
        if (client == null) client = RvaucMsService.createService(Client.class);
        return client;
    }
    interface Client {
        @POST("/auth/password-management/forgot-password")
        Call<VoidResponse> forgotPassword(@Body ForgotPasswordRequest request);
        @POST("/auth/password-management/verify-code")
        Call<VoidResponse> verifyCode(@Body VerifyCodeRequest request);
        @POST("/auth/password-management/reset-password")
        Call<VoidResponse> resetPassword(@Body ResetPasswordRequest request);
    }

    public static class ForgotPasswordRequest {
        public String email;
        public ForgotPasswordRequest() {}
    }
    public static class VerifyCodeRequest {
        public String email;
        public String code;
        public VerifyCodeRequest() {}
    }

    public static class ResetPasswordRequest {
        public String code;
        public String password;
        public String confirmPassword;
        public ResetPasswordRequest() {}
    }
}
