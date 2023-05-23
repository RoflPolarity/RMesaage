package com.example.rmesaage.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JpegCompressionExample {
    public static byte[] compress(byte[] image) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        return outputStream.toByteArray();
    }
}
