package com.example.rfid.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParser {
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        try {
            return new ObjectMapper().readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed converting JSON to object.", e);
        }
    }
    public static <T> String toJson(T object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed converting object to JSON: " + object.getClass().getSimpleName(), e);
        }
    }
}
