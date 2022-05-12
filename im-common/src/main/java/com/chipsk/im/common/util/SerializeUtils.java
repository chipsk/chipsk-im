package com.chipsk.im.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtils {

    public static byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream bai = new ByteArrayOutputStream();
            ObjectOutputStream obi = new ObjectOutputStream(bai);
            obi.writeObject(obj);
            byte[] byt = bai.toByteArray();
            return byt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static Object deserialize(byte[] byt) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(byt);
            ObjectInputStream oii = new ObjectInputStream(bis);
            Object obj = oii.readObject();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
