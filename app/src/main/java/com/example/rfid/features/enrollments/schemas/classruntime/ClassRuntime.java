package com.example.rfid.features.enrollments.schemas.classruntime;

import com.example.rfid.features.enrollments.schemas.shared.Cls;
import com.example.rfid.features.enrollments.schemas.shared.Course;
import com.example.rfid.features.enrollments.schemas.shared.Enrollment;
import com.example.rfid.features.enrollments.schemas.shared.Offering;
import com.example.rfid.features.enrollments.schemas.shared.Session;
import com.example.rfid.features.enrollments.schemas.shared.Professor;

public class ClassRuntime {
    public Cls cls;
    public Course course;
    public Offering offering;
    public Session session;
    public Professor professor;
    public Enrollment enrollment;

    public ClassRuntime() {}
}
