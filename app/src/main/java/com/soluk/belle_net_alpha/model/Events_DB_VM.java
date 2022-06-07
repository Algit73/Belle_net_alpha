package com.soluk.belle_net_alpha.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.soluk.belle_net_alpha.HTTP_Provider;
import com.soluk.belle_net_alpha.event_data_maker.file_maker;
import com.soluk.belle_net_alpha.event_data_maker.geo_JSON_maker;
import com.soluk.belle_net_alpha.Main_Activity;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;
import com.soluk.belle_net_alpha.utils.Image_Provider;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.os.Looper.getMainLooper;

public class Events_DB_VM extends ViewModel
{

    private static final String TAG = Main_Activity.class.getSimpleName();
    private static final String URL = "https://soluk.org/belle_net_users_info/depict_database.php";
    private static final String REQUEST_DB_SUB_URL = "belle_net_users_info/depict_database.php";
    public static final String REQUEST_PICS_SUB_URL = "belle_net_users_info/request_profile_images.php";
    public static final String profile_pic_url = "https://soluk.org/belle_net_users_info/profile_pictures/";
    //private static final String USER_ID = "#ahdx98!s5kjxsp";
    private boolean is_initiated = false;
    private boolean is_process_finished = false;
    private String db_file_directory;
    public  File image_file_directory;
    private static File image_file_directory_static;
    public static String file_directory_static = "";
    private static final String FILE_NAME = "geo_json_bellenet";
    protected db_update_handler db_handler;
    protected DB_Image_Callback db_image_callback;

    public int num_until_updating;
    public boolean is_data_received;
    public boolean is_callback_called;

    public static final String USER_NAME = "user_name";
    public static final String USER_FAMILY = "user_family";
    public static final String USER_NAME_FAMILY = "user_name_family";
    public static final String USER_PIC = "user_picture";
    public static final String USER_ID = "user_id";
    public static final String USER_ID_FOLLOWING = "user_id_following";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_PASSWORD = "user_password";
    public static final String USER_JOIN_DATE = "user_join_date";
    public static final String USER_FOLLOWINGS = "user_followings";
    public static final String USER_FOLLOWERS = "user_followers";
    public static final String USER_TYPE = "user_type";
    public static final String USER_COMMENT = "user_comment";
    public static final String USER_REPLY = "user_reply";
    public static final String USER_REQUEST = "user_request";
    public static final String USER_ACTION = "user_action";
    public static final String OWNER_USER_ID = "owner_user_id";
    public static final String EVENT_NAME = "event_name";
    public static final String EVENT_CREATION_DATE = "event_creation_date";
    public static final String EVENT_DATE = "event_date";
    public static final String EVENT_DATE_END = "event_date_end";
    public static final String EVENT_TIME = "event_time";
    public static final String EVENT_TIME_END = "event_time_end";
    public static final String EVENT_TYPE = "event_type";
    public static final String EVENT_ID = "event_id";
    public static final String COMMENT_ID = "comment_id";
    public static final String COMMENT_TAIL_ID = "comment_tail_id";
    public static final String COMMENT_TAIL_ID_R0 = "r0";
    public static final String COMMENT_REPLIES = "comment_replies";
    public static final String NUM_POINTS = "num_points";
    public static final String IS_USER_JOINED = "is_user_joined";
    public static final String NUM_OF_JOINED = "count";


    public void init_db(String db_file_directory,File image_file_directory)
    {
        /// Initiating DB just once
        if(!is_initiated)
        {
            refresh_db();
            this.db_file_directory = db_file_directory;
            this.image_file_directory = image_file_directory;
            image_file_directory_static = image_file_directory;
        }
    }

    public static File get_image_file()
    {
        return image_file_directory_static;
    }

    public void refresh_db()
    {

        Callback callback = new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                call(response);

            }
        };

        try
        {
            JSONObject json = new JSONObject();
            json.put("user_id",User_Credentials.get_item(USER_ID));
            HTTP_Provider.post_json(REQUEST_DB_SUB_URL,json,callback);
        }
        catch (Exception e){Log.d(TAG, "Data Base Call Error: " + e);}


    }

    private void call(Response response)
    {
        try
        {
            String response_body = response.body().string();
            Log.d(TAG, "Post Code: " + response.code());
            Log.d(TAG, "Data Base Call: " + response_body );

            if (response.code() != 200)
                return;
            Log.d(TAG, "Response: OK");
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
            },5000);



        }
        catch (Exception e)
        {
            Log.d(TAG, "Data Base Receive Error: " + e.getMessage());
        }


    }

    private void add_received_events(JSONArray received_events)
    {


        file_directory_static = db_file_directory;
        geo_JSON_maker feature_maker = new geo_JSON_maker();

        Log.d(TAG,"received_events.length: "+received_events.length());
        Set<String> unique_profile_pics = new TreeSet<>();
        for (int i=0;i<received_events.length();i++)
        {
            try
            {
                JSONObject event = new JSONObject(received_events.get(i).toString());
                unique_profile_pics.add(event.get("user_picture").toString());
                feature_maker.add_feature(event);
            }
            catch (Exception e)
            {
                Log.d(TAG,"data received: "+e.getMessage());
            }
            //Log.d(TAG,"db_image_callback: ");
        }


        /// Using internal sort
        //num_until_updating = unique_profile_pics.size();
        //try {for(String id : unique_profile_pics) cache_temp(id);}
        //catch (Exception e) {}

        /// Using Server sorting
        try {cache_profile_images("");}
        catch (Exception e) {}


        file_maker geo_json_holder = new file_maker(db_file_directory, FILE_NAME);
        geo_json_holder.write_json(feature_maker.get_features_object());

    }

    private void cache_profile_images(String postfix) throws JSONException
    {

        /// Using Server sorting
        Callback callback = new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                String body = response.body().string();
                Log.d(TAG,"cache array body: "+body);
                try
                {
                    JSONArray pics_array;
                    pics_array = new JSONArray(body);
                    Log.d(TAG,"cache array body: "+body);
                    num_until_updating = pics_array.length();
                    for(int i=0;i<pics_array.length();i++)
                        cache_temp(pics_array.get(i).toString());
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }


            }
        };
        JSONObject user_cred = new JSONObject();
        user_cred.put("user_email", User_Credentials.get_item("user_email"));
        user_cred.put("user_password", User_Credentials.get_item("user_password"));

        HTTP_Provider.post_json(REQUEST_PICS_SUB_URL,user_cred,callback);


    }

    private void cache_temp(String postfix)
    {
        Log.d(TAG,"catch_profile_images: Entered");
        Target target = new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {

                Log.d(TAG,"BMP Received");
                is_data_received = true;
                Image_Provider.save_to_internal_storage(bitmap,postfix);
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

        /// Accessing Main UI Thread
        Handler mainHandler = new Handler(getMainLooper());
        Runnable myRunnable = () ->
        {
            Picasso.get().invalidate(path);

            Picasso.get().load(path).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(target);
        };
        mainHandler.post(myRunnable);

        //Picasso.get().load(path).into(target);

        Log.d(TAG,"catch_profile_images: Exited");
    }



    /// Callback function to inform when DB get updated
    public void set_db_handler(db_update_handler db_handler)
    {
        this.db_handler = db_handler;
    }

    public void set_catch_images_callback(DB_Image_Callback db_image_callback)
    {
        this.db_image_callback = db_image_callback;
    }

}
