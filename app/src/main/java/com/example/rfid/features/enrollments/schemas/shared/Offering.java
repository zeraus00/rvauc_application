package com.example.rfid.features.enrollments.schemas.shared;

import androidx.annotation.Nullable;

public class Offering {
    public String weekDay;
    public String startTime;
    public String endTime;
    @Nullable
    public Room room;
    public Offering() {}
}
