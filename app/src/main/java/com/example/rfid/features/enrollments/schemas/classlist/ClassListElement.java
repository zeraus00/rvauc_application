package com.example.rfid.features.enrollments.schemas.classlist;

import androidx.annotation.Nullable;

import com.example.rfid.features.enrollments.schemas.shared.Cls;
import com.example.rfid.features.enrollments.schemas.shared.Course;
import com.example.rfid.features.enrollments.schemas.shared.Enrollment;
import com.example.rfid.features.enrollments.schemas.shared.Offering;
import com.example.rfid.features.enrollments.schemas.shared.Professor;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClassListElement {
    @JsonProperty("class")
    public Cls cls;
    public Course course;
    @Nullable
    public Offering offering;
    public Professor professor;
    public Enrollment enrollment;
    public ClassListElement() {}
}
