package com.example.rfid.features.violation.services;

import static com.example.rfid.services.RvaucMsService.rvaucMsCallback;

import com.example.rfid.dto.ApiResponse;
import com.example.rfid.interfaces.HttpCallback;
import com.example.rfid.services.RvaucMsService;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public class ViolationService {
    private static Client client;
    public static void getViolationList(HttpCallback<ViolationListResponse> callback) {
        getClient().getViolationList().enqueue(rvaucMsCallback(callback));
    }
    private static Client getClient() {
        if (client == null) client = RvaucMsService.createService(Client.class);
        return client;
    }
    interface Client {
        @Headers("X-Inject-Auth: true")
        @GET("/violation/view-records")
        Call<ViolationListResponse> getViolationList();
    }
    public static class ViolationListResponse extends ApiResponse<ViolationList> {}
    public static class ViolationList {
        public ViolationRecord[] violationRecords;
        public ViolationList() {}
    }
    public static class ViolationRecord {

        public int id;
        public String date;
        public String day;

        // Expected format: HH:mm (24-hour)
        public String time;

        public String status;

        public String[] reasons;

        // student number as string
        public String studentNumber;

        public String block;
        public int yearLevel;
        public String department;

        public String surname;
        public String firstName;

        // nullish().default("")
        public String middleName = "";

        public ViolationRecord() {
        }
    }

}
