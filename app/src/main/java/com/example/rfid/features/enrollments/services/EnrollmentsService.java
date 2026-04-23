package com.example.rfid.features.enrollments.services;

import static com.example.rfid.services.RvaucMsService.rvaucMsCallback;

import com.example.rfid.dto.ApiResponse;
import com.example.rfid.features.enrollments.schemas.classattendance.ClassAttendance;
import com.example.rfid.features.enrollments.schemas.scheduledclasseswithprofessor.ClassesWithProfessor;
import com.example.rfid.interfaces.HttpCallback;
import com.example.rfid.services.RvaucMsService;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public class EnrollmentsService {
    private static Client client;

    public static void getAttendanceList(int classId, HttpCallback<ClassAttendanceResponse> callback) {
        getClient().getAttendanceList(classId).enqueue(rvaucMsCallback(callback));
    }

    public static void getClassList(HttpCallback<ClassesWithProfessorResponse> callback) {
        getClient().getClassList().enqueue(rvaucMsCallback(callback));
    }
    public static void getSchedule(HttpCallback<ClassesWithProfessorResponse> callback) {
        getClient().getSchedule().enqueue(rvaucMsCallback(callback));
    }
    private static Client getClient() {
        if (client == null) client = RvaucMsService.createService(Client.class);
        return client;
    }
    interface  Client {

        @Headers("X-Inject-Auth: true")
        @GET("/enrollments/attendance/records/class/{classId}")
        Call<ClassAttendanceResponse> getAttendanceList(@Path("classId") int classId);
        @Headers("X-Inject-Auth: true")
        @GET("/enrollments/schedule/get-class-list")
        Call<ClassesWithProfessorResponse> getClassList();

        @Headers("X-Inject-Auth: true")
        @GET("/enrollments/schedule/get-schedule")
        Call<ClassesWithProfessorResponse> getSchedule();
    }

    public static class ClassAttendanceResponse extends ApiResponse<ClassAttendance> {}
    public static class ClassesWithProfessorResponse extends ApiResponse<ClassesWithProfessor>{}


}
