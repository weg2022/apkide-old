package com.apkide.engine;

import com.apkide.analysis.api.clm.callback.FileReaderCallback;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;

public class FileReaderCallbackImpl implements FileReaderCallback {
    @Override
    public Reader createBomReader(InputStream inStream, String fallbackEncoding) throws IOException {
        PushbackInputStream pbInputStream = new PushbackInputStream(inStream, 4);
        byte[] bomBytes = new byte[4];

        int totalCount;
        int count;
        for (totalCount = 0; totalCount < 4; totalCount += count) {
            count = pbInputStream.read(bomBytes, totalCount, 4 - totalCount);
            if (count == -1) {
                break;
            }
        }

        String encoding = null;
        if (totalCount == 4) {
            if ((bomBytes[0] & 255) == 0 && (bomBytes[1] & 255) == 0 && (bomBytes[2] & 255) == 254 && (bomBytes[3] & 255) == 255) {
                encoding = "UTF-32BE";
            } else if ((bomBytes[0] & 255) == 255 && (bomBytes[1] & 255) == 254 && (bomBytes[2] & 255) == 0 && (bomBytes[3] & 255) == 0) {
                encoding = "UTF-32LE";
            }
        }

        if (encoding == null && totalCount >= 3 && (bomBytes[0] & 255) == 239 && (bomBytes[1] & 255) == 187 && (bomBytes[2] & 255) == 191) {
            encoding = "UTF-8";
            pbInputStream.unread(bomBytes, 3, totalCount - 3);
        }

        if (encoding == null && totalCount >= 2) {
            if ((bomBytes[0] & 255) == 254 && (bomBytes[1] & 255) == 255) {
                encoding = "UTF-16BE";
            } else if ((bomBytes[0] & 255) == 255 && (bomBytes[1] & 255) == 254) {
                encoding = "UTF-16LE";
                pbInputStream.unread(bomBytes, 2, totalCount - 2);
            }
        }

        if (encoding == null) {
            pbInputStream.unread(bomBytes, 0, totalCount);
        }

        if (encoding == null) {
            return fallbackEncoding != null ? new InputStreamReader(pbInputStream, fallbackEncoding) : new InputStreamReader(pbInputStream);
        } else {
            return new InputStreamReader(pbInputStream, encoding);
        }
    }
}
