package com.example.rfid.features.enrollments.schemas.classlist;

import androidx.annotation.Nullable;

public class Offering {
    public int id;
    public String weekDay;
    public String startTime;
    public String endTime;
    @Nullable
    public Room room;
    public Offering() {}
}
