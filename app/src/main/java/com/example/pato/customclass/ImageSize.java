package com.example.pato.customclass;

import android.graphics.BitmapFactory;
import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;

public class ImageSize {

    public static BitmapFactory.Options getBitmapSize(File imageFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        return options;
    }

    public static String ImageSizeMethod(String filePath, File file){
        ExifInterface exifInterface = null;
        BitmapFactory.Options options = getBitmapSize(file);

        String size = "";
        try {
            exifInterface = new ExifInterface(filePath);
            size = options.outHeight + ","+ options.outWidth + "," + exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return size;
    }
}
