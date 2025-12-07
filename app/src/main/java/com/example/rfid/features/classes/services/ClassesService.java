package com.example.rfid.features.classes.services;

import static com.example.rfid.services.RvaucMsService.rvaucMsCallback;

import com.example.rfid.dto.ApiResponse;
import com.example.rfid.interfaces.HttpCallback;
import com.example.rfid.services.RvaucMsService;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public class ClassesService {
    private static Client client;

    public static void getClassList(HttpCallback<ClassListResponse> callback) {
        getClient().getClassList().enqueue(rvaucMsCallback(callback));
    }
    public static void getSchedule(HttpCallback<ClassListResponse> callback) {
        getClient().getSchedule().enqueue(rvaucMsCallback(callback));
    }
    private static Client getClient() {
        if (client == null) client = RvaucMsService.createService(Client.class);
        return client;
    }
    interface  Client {
        @Headers("X-Inject-Auth: true")
        @GET("/enrollments/schedule/get-class-list")
        Call<ClassListResponse> getClassList();

        @Headers("X-Inject-Auth: true")
        @GET("/enrollments/schedule/get-schedule")
        Call<ClassListResponse> getSchedule();
    }

    public static class ClassListResponse extends ApiResponse<ClassList>{}
    public static class ClassList {
        public ClassRecord[] classList;
    }

    public static class ClassRecord {

        // Optional field with default -1
        public int enrollmentId = -1;

        // Class metadata
        public int id;
        public String weekDay;
        public String startTimeText;
        public String endTimeText;
        public long startTime;
        public long endTime;
        public String classNumber;

        // Course metadata
        public String courseCode;
        public String courseName;

        // Professor metadata
        public Professor professor;

        // Empty constructor
        public ClassRecord() {
        }

        // Nested static Professor class
        public static class Professor {
            public String surname;
            public String firstName;
            public String middleName; // nullable

            // Empty constructor
            public Professor() {
            }
        }
    }
}
