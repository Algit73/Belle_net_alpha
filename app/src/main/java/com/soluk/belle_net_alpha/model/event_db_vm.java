package com.soluk.belle_net_alpha.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.ViewModel;

import com.soluk.belle_net_alpha.event_data_maker.file_maker;
import com.soluk.belle_net_alpha.event_data_maker.geo_JSON_maker;
import com.soluk.belle_net_alpha.http_requests.SimpleHttp;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class event_db_vm extends ViewModel
{

    private static final String TAG = event_db_vm.class.getSimpleName();
    private static final String URL = "https://soluk.org/belle_net_users_info/depict_database.php";
    private static final String profile_pic_url = "https://soluk.org/belle_net_users_info/profile_pictures/%23";
    private static final String USER_ID = "#ahdx98!s5kjxsp";
    private boolean is_initiated = false;
    private boolean is_process_finished = false;
    private String db_file_directory;
    private File image_file_directory;
    public static String file_directory_static = "";
    private static final String FILE_NAME = "geo_json_bellenet";
    protected db_update_handler db_handler;

    private int num_until_updating;
    private boolean is_data_received;
    private boolean is_callback_called;


    public void init_db(String db_file_directory,File image_file_directory)
    {
        /// Initiating DB just once
        if(!is_initiated)
        {
            refresh_db();
            this.db_file_directory = db_file_directory;
            this.image_file_directory = image_file_directory;
        }
    }

    public void refresh_db()
    {

        Map<String, String> params = new HashMap<>();
        params.put("user_id", USER_ID);

        SimpleHttp.post(URL, params, (result_code, response_body) ->
        {
            try
            {
                Log.d(TAG, "Post Code: " + result_code);
                Log.d(TAG, "Post Body: " + response_body);
                if (result_code != 200)
                    return;
                JSONArray received_events = new JSONArray(response_body);
                is_initiated = true;
                is_data_received = false;
                is_callback_called = false;
                add_received_events(received_events);

                // TODO: Finding the problem with unusual unsuccessful image loading
                new Handler(Looper.getMainLooper()).postDelayed(()->
                {
                    Log.d(TAG, "Requesting Data Once Again: ");
                    if(!is_data_received)
                    {
                        Log.d(TAG, "Requesting Data Once Again: Requested");
                        add_received_events(received_events);
                    }
                    else
                    {

                        if(!is_callback_called)
                        {
                            Log.d(TAG, "CallBack Called");
                            is_callback_called = true;
                            db_handler.on_db_updated();
                        }
                    }
                },10000);


                //db_handler.on_db_updated();
                //data_received_correctly=true;
            }
            catch (Exception e)
            {
                Log.d(TAG, "on Post Response: " + e.getMessage());
            }
        });

    }

    private void add_received_events(JSONArray received_events)
    {


        file_directory_static = db_file_directory;
        geo_JSON_maker feature_maker = new geo_JSON_maker();
        num_until_updating = received_events.length();
        for (int i=0;i<received_events.length();i++)
        {
            try
            {
                JSONObject event = new JSONObject(received_events.get(i).toString());
                catch_profile_images(event.get("user_picture").toString().substring(1));
                Log.d(TAG,"profile prefix: "+event.get("user_picture").toString().substring(1));
                feature_maker.add_feature(event);

            }
            catch (Exception e)
            {
                Log.d(TAG,"data received: "+e.getMessage());
            }

        }

        file_maker geo_json_holder = new file_maker(db_file_directory, FILE_NAME);
        geo_json_holder.write(feature_maker.get_features_object());
        //geo_json_holder.read();


    }

    private void catch_profile_images(String postfix)
    {

        Log.d(TAG,"catch_profile_images: Entered");
        Target target = new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {

                Log.d(TAG,"BMP Received");
                is_data_received = true;
                saveToInternalStorage(bitmap,postfix);
                Log.d(TAG,"image postfix:"+postfix);
                Log.d(TAG,"image num_until_updating:"+num_until_updating);
                /// Check if the last image received
                num_until_updating--;
                if(num_until_updating==0)
                {
                    /// Calling the callback function in the activity
                    Log.d(TAG, "Loaded Last Received");
                    db_handler.on_db_updated();
                    is_callback_called = true;
                }

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable)
            {
                Log.d(TAG,"BMP Failed");
                is_data_received = true;
                /// Check if the last image received
                num_until_updating--;
                if(num_until_updating==0)
                {
                    /// Calling the callback function in the activity
                    Log.d(TAG, "Failed Last Received");
                    db_handler.on_db_updated();
                    is_callback_called = true;
                }
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable)
            {
                Log.d(TAG,"BMP onPrepareLoad");
            }
        };
        String path = profile_pic_url + postfix+ ".jpg";
        Log.d(TAG,"profile path: "+path);

        Picasso.get().load(path).into(target);

        Log.d(TAG,"catch_profile_images: Exited");
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String name)
    {
        //ContextWrapper context_wrapper = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        //File directory = context_wrapper.getDir("Profile_Pictures", Context.MODE_PRIVATE);
        // Create imageDir
        File path=new File(image_file_directory,name);
        Log.d(TAG,"Profile pic address "+path);

        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(path);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);
        }
        catch (Exception e)
        {
            Log.d(TAG,"Int Strge Create File failed: "+e.getMessage());
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
        Log.d(TAG,"Directory: "+ image_file_directory.getAbsolutePath());
        return image_file_directory.getAbsolutePath();
    }

    /// Callback function to inform when DB get updated
    public void set_db_handler(db_update_handler db_handler)
    {
        this.db_handler = db_handler;
    }

}
