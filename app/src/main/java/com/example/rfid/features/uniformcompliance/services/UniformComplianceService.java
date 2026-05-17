package com.example.rfid.features.uniformcompliance.services;

import static com.example.rfid.services.RvaucMsService.rvaucMsCallback;

import com.example.rfid.dto.ApiResponse;
import com.example.rfid.interfaces.HttpCallback;
import com.example.rfid.services.RvaucMsService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public class UniformComplianceService {
    private static Client client;
    public static void viewRecords(HttpCallback<HistoryResponse> callback) {
        getClient().viewRecords().enqueue(rvaucMsCallback(callback));
    }
    private static Client getClient() {
        if (client == null) client = RvaucMsService.createService(Client.class);
        return client;
    }
    interface Client {
        @Headers("X-Inject-Auth: true")
        @GET("enrollments/uniform-compliance/records")
        Call<HistoryResponse> viewRecords();
    }
    public static class RecordResponse extends ApiResponse<Record[]>{}
    public static class Record {
        public int id;
        public String date;
        public String day;
        public String time;
        public String status;
        public String studentNumber;
        public String block;
        public int yearLevel;
        public String department;
        public String surname;
        public String firstName;
        public String middleName;
        public Record() {}
    }

    public static class HistoryResponse extends  ApiResponse<History> {}
    public static class History {
        public ArrayList<HistoryItem> history;
    }
    public static class HistoryItem {
        public _Record record;
        public String uniformType;
        public ArrayList<String> missing;
    }
    public static class _Record {
        public int id;
        public String datePh;
        public String weekDay;
        public String time;
        public boolean isCompliant;
        public String status;
        public long recordedMs;
    }
}
