package com.example.workstasion.myapplication.Workers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.provider.MediaStore;

import java.io.File;
import java.io.RandomAccessFile;
import java.security.SecureRandom;

/**
 * Created by WORKSTASION on 15.12.2016.
 */

public class Tasks {


    public static boolean rmDir( String dirPath ){

        File file = new File(dirPath);
        if(file.exists()){
            if(file.isDirectory())
                for(File child : file.listFiles())
                    rmDir(child.getPath());

            return file.delete();
        }return false;
    }

    public static Bitmap decodeSampledBitmapFromResource(String filePath,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;

        Bitmap btm = BitmapFactory.decodeFile(filePath,options);

        try {
            int rotation = exifToDegrees(new ExifInterface(filePath).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
            Matrix matrix = new Matrix();
            if(rotation != 0){ matrix.postRotate(rotation); }
            btm = Bitmap.createBitmap(btm, 0, 0, btm.getWidth(), btm.getHeight(), matrix, true);
        }catch (Exception ex){
            ex.printStackTrace();
        } return btm;
    }

    private static int exifToDegrees(int exifOrientation) {
        switch (exifOrientation){
            case ExifInterface.ORIENTATION_ROTATE_90 : return 90;
            case ExifInterface.ORIENTATION_ROTATE_180 : return 180;
            case ExifInterface.ORIENTATION_ROTATE_270 : return 270;
        } return 0;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
