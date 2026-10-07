package com.xxxx.secondkill.utils;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JSONUtilTest {

    @Test
    void serializesObjectToJsonString() {
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("name", "秒杀商品");
        value.put("stock", 10);

        assertEquals(
                "{\"name\":\"秒杀商品\",\"stock\":10}",
                JSONUtil.object2JsonStr(value)
        );
    }

    @Test
    void returnsNullForNullInput() {
        assertNull(JSONUtil.object2JsonStr(null));
    }

    @Test
    void deserializesJsonStringToObject() {
        TestPayload result = JSONUtil.jsonStr2Object(
                "{\"name\":\"秒杀商品\",\"stock\":10}",
                TestPayload.class
        );

        assertEquals("秒杀商品", result.name);
        assertEquals(10, result.stock);
    }

    @Test
    void returnsNullForBlankJsonString() {
        assertNull(JSONUtil.jsonStr2Object("  ", TestPayload.class));
    }

    @Test
    void rejectsInvalidJsonString() {
        assertThrows(
                IllegalArgumentException.class,
                () -> JSONUtil.jsonStr2Object("not-json", TestPayload.class)
        );
    }

    @Test
    void wrapsSerializationFailure() {
        assertThrows(
                IllegalStateException.class,
                () -> JSONUtil.object2JsonStr(new Object())
        );
    }

    static class TestPayload {
        public String name;
        public int stock;
    }
}
