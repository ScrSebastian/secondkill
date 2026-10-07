package com.xxxx.secondkill.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.Objects;

/**
 * JSON serialization utilities.
 */
public final class JSONUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private JSONUtil() {
    }

    /**
     * Serializes an object as JSON.
     *
     * @param object object to serialize
     * @return JSON text, or {@code null} when the input is {@code null}
     * @throws IllegalStateException when the object cannot be serialized
     */
    public static String object2JsonStr(Object object) {
        if (object == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize object to JSON", exception);
        }
    }

    /**
     * Deserializes JSON text into an object of the requested type.
     *
     * @param jsonStr JSON text
     * @param targetType target object type
     * @param <T> target object type
     * @return deserialized object, or {@code null} when the JSON text is blank
     * @throws NullPointerException when {@code targetType} is {@code null}
     * @throws IllegalArgumentException when the JSON text cannot be deserialized
     */
    public static <T> T jsonStr2Object(String jsonStr, Class<T> targetType) {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return null;
        }

        Objects.requireNonNull(targetType, "Target type must not be null");

        try {
            return OBJECT_MAPPER.readValue(jsonStr, targetType);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Failed to deserialize JSON", exception);
        }
    }
}
