package com.example.rfid.features.enrollments.schemas.classlist;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClassListElement {
    @JsonProperty("class")
    public Cls cls;
    public Course course;
    @Nullable
    public Offering offering;
    public Professor professor;
    public ClassListElement() {}
}
