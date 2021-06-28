package com.soluk.belle_net_alpha.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.soluk.belle_net_alpha.model.Events_DB_VM;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Image_Provider
{

    private static File file;

    public static void set_file(File file)
    {
        Image_Provider.file = file;
    }

    public static File get_file()
    {
        return file;
    }



    public Image_Provider()
    {

    }

    public static Bitmap get_profile_bmp(String name)
    {
        try
        {
            //File file =new File(Image_Provider.get_file(), name);
            Log.d("map_fragment","get_profile_bmp Entered");
            File file =new File(Events_DB_VM.get_image_file(), name);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            return bitmap;
        }
        catch (FileNotFoundException e)
        {
            Log.d("map_fragment","get_profile_bmp: "+e);
            e.printStackTrace();
            return null;
        }
    }

    public static String save_to_internal_storage(Bitmap bitmapImage, String name)
    {
        File path=new File(Image_Provider.file,name);
        //Log.d(TAG,"Profile pic address "+path);

        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(path);
            // Use the compress method on the BitMap object to write_json image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        }
        catch (Exception e)
        {
            //Log.d(TAG,"Int Strge Create File failed: "+e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            try
            {
                fos.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        //Log.d(TAG,"Directory: "+ image_file_directory_static.getAbsolutePath());
        return Image_Provider.file.getAbsolutePath();
    }


}
