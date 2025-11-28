package com.example.rfid.services;

import static com.example.rfid.utils.JsonParser.fromJson;

import androidx.annotation.NonNull;

import com.example.rfid.dto.ApiResponse;
import com.example.rfid.interfaces.HttpCallback;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestService {
    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json");

    public void post(String url, String json, HttpCallback callback) {

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException  {
                if(response.isSuccessful()) {
                    callback.onSuccess(response.body().string());
                } else {
                    var res = fromJson(response.body().string(), new TypeReference<ApiResponse<Void>>() {});
                    callback.onError(new Exception(res.message));
                }
            }
        });

    }
}
