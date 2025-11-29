package com.example.rfid.services;

import androidx.annotation.NonNull;

import com.example.rfid.dto.ApiResponse;
import com.example.rfid.interfaces.HttpCallback;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Callback;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RvaucMsService {
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:2620")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit;
    }

    public static <T> T createService(Class<T> classRef) {
        return getClient().create(classRef);
    }

    public static <R, T extends ApiResponse<R>> Callback<T> rvaucMsCallback(HttpCallback<T> callback) {
        return new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                var body = response.body();

                if(response.isSuccessful()) {
                    callback.onSuccess(body);
                } else {
                    String message = body != null ? body.message : "Something went wrong.";
                    callback.onError(message);
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        };
    }
}
