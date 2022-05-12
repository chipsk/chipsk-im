package com.chipsk.im.common.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class SerializeUtil {
    private static class SerializeData{
        private Object target;
    }
    @SuppressWarnings("unchecked")
    public static byte[] serialize(Object object) {
        SerializeData serializeData = new SerializeData();
        serializeData.target = object;
        Class<SerializeData> serializeDataClass = (Class<SerializeData>) serializeData.getClass();
        LinkedBuffer linkedBuffer = LinkedBuffer.allocate(1024 * 4);
        try {
            Schema<SerializeData> schema = RuntimeSchema.getSchema(serializeDataClass);
            return ProtostuffIOUtil.toByteArray(serializeData, schema, linkedBuffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            linkedBuffer.clear();
        }
    }
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] data, Class<T> clazz) {
        try {
            Schema<SerializeData> schema = RuntimeSchema.getSchema(SerializeData.class);
            SerializeData serializeData = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(data, serializeData, schema);
            return (T) serializeData.target;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
