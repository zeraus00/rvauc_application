package com.example.rfid.services;

import androidx.annotation.NonNull;

import com.example.rfid.dto.ApiResponse;
import com.example.rfid.interfaces.HttpCallback;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Callback;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RvaucMsService {
    private static Retrofit retrofit;

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
    public static <T> T createService(Class<T> classRef) {
        return getRetrofitClient().create(classRef);
    }

    public static Retrofit getRetrofitClient() {
        if (retrofit == null) throw new RuntimeException("RvaucMs client was not initialized.");
        return retrofit;
    }
    public static <TInterceptor extends Interceptor> void init(TInterceptor[] interceptors) {
        var retrofitBuilder = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:2620")
                .addConverterFactory(JacksonConverterFactory.create());

        if (interceptors.length > 0) {
            var okHttpClientBuilder = new OkHttpClient.Builder();
            for (TInterceptor interceptor : interceptors) {
                okHttpClientBuilder.addInterceptor(interceptor);
            }
            var okHttpClient = okHttpClientBuilder.build();
            retrofitBuilder.client(okHttpClient);
        }

        retrofit = retrofitBuilder.build();
    }
}
