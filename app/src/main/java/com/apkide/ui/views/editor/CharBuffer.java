package com.apkide.ui.views.editor;

class CharBuffer {

    public static char[] obtain(int len) {
        char[] buf;

        synchronized (CharBuffer.class) {
            buf = sTemp;
            sTemp = null;
        }

        if (buf == null || buf.length < len) {
            buf = new char[len];
        }

        return buf;
    }


    public static void recycle(char[] temp) {
        if (temp.length > 8192) return;

        synchronized (CharBuffer.class) {
            sTemp = temp;
        }
    }

    private static char[] sTemp = null;


}
