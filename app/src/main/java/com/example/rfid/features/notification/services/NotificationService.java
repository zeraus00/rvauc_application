package com.example.rfid.features.notification.services;

import static com.example.rfid.services.RvaucMsService.rvaucMsCallback;

import com.example.rfid.dto.ApiResponse;
import com.example.rfid.interfaces.HttpCallback;
import com.example.rfid.services.RvaucMsService;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public class NotificationService {
    private static Client client;

    public static void getNotifications(HttpCallback<RecordResponse> callback) {
        getClient().getNotifications().enqueue(rvaucMsCallback(callback));
    }
    private static Client getClient() {
        if (client == null) client = RvaucMsService.createService(Client.class);
        return client;
    }

    interface Client {
        @Headers("X-Inject-Auth: true")
        @GET("/notifications/get-notifications")
        Call<RecordResponse> getNotifications();

    }
    public static class RecordResponse extends ApiResponse<Record[]> {}
    public static class Record {
        public int id;
        public String title;
        public String message;
        public boolean isRead = false;
        public String sentAt;

        public Record() {}
    }
}
