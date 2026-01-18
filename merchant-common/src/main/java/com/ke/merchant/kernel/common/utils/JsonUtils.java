package com.ke.merchant.kernel.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private JsonUtils() {
    }

    public static String obj2Str(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("json serialize failed", e);
        }
    }

    public static JsonNode obj2JsonNode(Object obj) {
        return MAPPER.valueToTree(obj);
    }

    public static JsonNode str2JsonNode(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("json parse failed", e);
        }
    }

    public static <T> T str2Obj(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("json parse failed", e);
        }
    }

    public static <T> T str2Obj(String json, TypeReference<T> typeRef) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("json parse failed", e);
        }
    }

    public static <T> T str2Obj(String json, Class<? extends java.util.Collection> collectionClass, Class<T> elementClass) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(collectionClass, elementClass));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("json parse failed", e);
        }
    }

    public static <T> java.util.List<T> str2List(String json, Class<T> elementClass) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(java.util.List.class, elementClass));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("json parse failed", e);
        }
    }

    public static ObjectMapper mapper() {
        return MAPPER;
    }

    public static ObjectMapper getObjectMapper() {
        return MAPPER;
    }
}
