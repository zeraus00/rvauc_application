package com.example.rfid.features.enrollments.schemas.classattendance;

import com.example.rfid.features.enrollments.schemas.shared.Offering;
import com.example.rfid.features.enrollments.schemas.shared.Session;

public class HistoryElement {
    public AttendanceRecord record;
    public Offering offering;
    public Session session;

    public HistoryElement() {}
}
