package com.example.rfid.features.uniformcompliance.services;

import java.util.List;

public class ViolationDTO {
    public int id;
    public String date;
    public String day;
    public String time;
    public List<String> reasons;

    public ViolationDTO() {}
}
