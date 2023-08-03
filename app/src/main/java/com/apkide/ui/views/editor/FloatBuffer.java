package com.apkide.ui.views.editor;

class FloatBuffer {

    public static float[] obtain(int len) {
        float[] buf;

        synchronized (FloatBuffer.class) {
            buf = sTemp;
            sTemp = null;
        }

        if (buf == null || buf.length < len) {
            buf = new float[len];
        }

        return buf;
    }


    public static void recycle(float[] temp) {
        if (temp.length > 8192) return;

        synchronized (FloatBuffer.class) {
            sTemp = temp;
        }
    }

    private static float[] sTemp = null;


}
