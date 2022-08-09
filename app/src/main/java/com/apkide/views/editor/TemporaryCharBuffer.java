package com.apkide.views.editor;

 class TemporaryCharBuffer {

     static char[] obtain(int len) {
        char[] buf;

        synchronized (TemporaryCharBuffer.class) {
            buf = sTemp;
            sTemp = null;
        }

        if (buf == null || buf.length < len)
            buf = new char[len];
        return buf;
    }

     static void recycle(char[] temp) {
        if (temp.length > 1000) return;

        synchronized (TemporaryCharBuffer.class) {
            sTemp = temp;
        }
    }

    private static char[] sTemp = null;
}
