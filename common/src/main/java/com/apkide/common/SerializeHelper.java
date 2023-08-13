package com.apkide.common;



import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeHelper {

    @Nullable
    public static Object deserialize(@Nullable byte[] serialized, @Nullable Object def) {
        if (serialized != null) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
                ObjectInputStream oInputStream = new ObjectInputStream(bis);
                Object res = oInputStream.readObject();
                if (res != null) {
                    return res;
                }
            } catch (Exception ignored) {
            }
        }
        return def;
    }


    @Nullable
    public static byte[] serialize(@Nullable Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(obj);
            os.close();
            return bos.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
}
