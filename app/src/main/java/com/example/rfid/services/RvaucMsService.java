package com.example.rfid.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.rfid.dto.ApiError;
import com.example.rfid.dto.ApiResponse;
import com.example.rfid.interfaces.HttpCallback;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Callback;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RvaucMsService {
    private static Retrofit retrofit;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <R, T extends ApiResponse<R>> Callback<T> rvaucMsCallback(HttpCallback<T> callback) {
        return new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if(response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    String message = "Something went wrong";

                    try {
                        try (var errorBody = response.errorBody()) {

                            if (errorBody != null) {
                                var json = errorBody.string();

                                var mapped = mapper.readValue(json, ApiError.class);

                                message = mapped.message;
                            }
                        }
                    } catch (IOException e) {
                        Log.e("ApiErrorParser", "Failed to parse error", e);
                    }

                    callback.onError(message);
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        };
    }
    public static <T> T createService(Class<T> classRef) {
        return getRetrofitClient().create(classRef);
    }

    public static Retrofit getRetrofitClient() {
        if (retrofit == null) throw new RuntimeException("RvaucMs client was not initialized.");
        return retrofit;
    }

    public static <TInterceptor extends Interceptor> void init(TInterceptor[] interceptors, Authenticator authenticator) {
        var retrofitBuilder = new Retrofit.Builder()
                .baseUrl("http://192.168.100.6:2620")
                .addConverterFactory(JacksonConverterFactory.create());

        var okHttpClientBuilder = new OkHttpClient.Builder()
                .authenticator(authenticator);

        for (TInterceptor interceptor : interceptors) {
            okHttpClientBuilder.addInterceptor(interceptor);
        }

        var okHttpClient = okHttpClientBuilder.build();

        retrofitBuilder.client(okHttpClient);

        retrofit = retrofitBuilder.build();
    }
}
