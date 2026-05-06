package com.example.rfid.features.enrollments.schemas.classlist;

import androidx.annotation.Nullable;

import com.example.rfid.features.enrollments.schemas.shared.Room;

public class Offering {
    public String weekDay;
    public int weekDayNumeric;
    public String startTime;
    public String endTime;
    @Nullable
    public Room room;
    public Offering() {}
}
