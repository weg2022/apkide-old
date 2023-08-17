package com.apkide.apktool.androlib.res.decoder.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.io.OutputStream;

public class ImageIO {
    
    public static BufferedImage read(InputStream is) {
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return new BufferedImage(bitmap);
    }
    
    public static void write(BufferedImage image, String fileType, OutputStream out) {
        fileType = fileType.toLowerCase();
        Bitmap.CompressFormat format;
        switch (fileType) {
            case "jpeg":
                format = Bitmap.CompressFormat.JPEG;
                break;
            case "webp":
                format = Bitmap.CompressFormat.WEBP;
                break;
            case "png":
            default:
                format = Bitmap.CompressFormat.PNG;
                break;
        }
        image.getBitmap().compress(format, 100, out);
    }
}
