package com.isma.soli.ad.Util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class StoreImageClass
{

    public Bitmap ConvertUriIntoBitmap(Uri uri, Context context)
    {
        Bitmap bitmap= null;


        try
        {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return bitmap;

    }
    //TODO
    public Bitmap ResizeBitmap (Bitmap bitmap)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //pour créer la miniature on comprime !
        float bitmapRatio = (float)width / (float) height;

        int TailleMin = 100;
        int TailleMax = 2400;

        if (bitmapRatio > 1) {
            width = 2400;
            height = (int) (width / bitmapRatio);
        } else {
            height = 2400;
            width = (int) (height * bitmapRatio);
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);


        return bitmap;
    }
    public void SaveImageIntoMemory(String ID, Bitmap thumb_bitmap, Context context) {

        Bitmap bitmap;
        bitmap = thumb_bitmap;
        String FilePath;

        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(StaticValues.DirectoryImageName, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir

        FilePath = ID + ".jpg";

        final File myImageFile = new File(directory, FilePath); // Create image file

        if (myImageFile.exists())
        {
            myImageFile.delete();
        }
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(myImageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, fos);
            Log.i("HelpServicesImage", "Compress");

        } catch (IOException e) {
            e.printStackTrace();
        } finally
        {

            try
            {
                Log.i("HelpServicesImage", "Closed");
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
