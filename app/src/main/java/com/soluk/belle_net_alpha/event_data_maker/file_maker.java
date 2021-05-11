package com.soluk.belle_net_alpha.event_data_maker;

import android.content.ContextWrapper;
import android.util.Log;

import com.mapbox.geojson.FeatureCollection;
import com.soluk.belle_net_alpha.main_activity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class file_maker
{
    private File file;
    private FileReader file_reader ;
    private FileWriter file_writer ;
    private BufferedReader buffered_reader ;
    private BufferedWriter buffered_writer;
    private static final String TAG = main_activity.class.getSimpleName();


    public file_maker(String parent, String file_name)
    {
        file = new File(parent,file_name);

        if(!file.exists())
        {
            //Timber.tag("TAG").v("File does not Exists");
            Log.v("TAG","file_maker: File does not Exists");
            try
            {
                file.createNewFile();
                file_writer = new FileWriter(file.getAbsoluteFile());
                buffered_writer = new BufferedWriter(file_writer);
                buffered_writer.write("{}");
                buffered_writer.close();
            }

            catch (Exception e)
            {
                //Timber.tag("TAG").v(e);
                Log.v("TAG",e.getMessage());
            }
        }
        else
            //Timber.tag("TAG").v("File Exists");
            Log.v("TAG","File Exists");
    }

    public JSONObject read ()
    {
        try
        {
            StringBuffer output = new StringBuffer();
            String response = null;
            file_reader = new FileReader(file.getAbsolutePath());
            buffered_reader = new BufferedReader(file_reader);
            String line = "";

            while ((line = buffered_reader.readLine()) != null)
            {
                output.append(line + "\n");
                Log.d(TAG, "file_maker output of read: "+line);
            }
            response = output.toString();
            return new JSONObject(response);
        }
        catch (Exception e)
        {
            Log.d(TAG, "file_maker output of read: "+e.getMessage());
            return null;
        }
    }

    public FeatureCollection read_features()
    {
        return FeatureCollection.fromJson(read().toString());
    }

    public void write(JSONObject object)
    {
        try
        {
            Log.v(TAG, "file_maker: Writing 1");
            file_writer = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(file_writer);
            bw.write(object.toString());
            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.v(TAG, "file_maker: Writing 2 "+e.getMessage());
        }
    }
}
