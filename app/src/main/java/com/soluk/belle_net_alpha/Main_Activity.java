package com.soluk.belle_net_alpha;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
//import androidx.appcompat.app.ActionBar;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.soluk.belle_net_alpha.main_fragments.Profile_Fragment;
import com.soluk.belle_net_alpha.main_fragments.live_events_feed_fragment;
import com.soluk.belle_net_alpha.main_fragments.Map_Fragment;
import com.soluk.belle_net_alpha.model.Events_DB_VM;
import com.soluk.belle_net_alpha.search_users.Search_Users_Activity;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;
import com.soluk.belle_net_alpha.utils.Image_Provider;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;

import okhttp3.Response;


public class Main_Activity extends AppCompatActivity implements
        PermissionsListener
{

    private static final String TAG = Main_Activity.class.getSimpleName();


    private FragmentTransaction fragment_transaction;
    private Map_Fragment mapFragment_instance;
    private live_events_feed_fragment event_list_parent_holder;
    private Profile_Fragment profile_fragment;

    private BottomNavigationView bottom_navigation_view;

    private Bitmap image_profile_bitmap;
    private Events_DB_VM model_db;

    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private List<String> missingPermission = new ArrayList<>();
    private static final int REQUEST_PERMISSION_CODE = 731;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted)
                {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    Log.d(TAG,"requestPermissionLauncher: Granted");
                }
                else
                {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Log.d(TAG,"requestPermissionLauncher: Not Granted");
                }
            });


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);



        /*
        /// Change status bar color
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = AppCompatResources.getDrawable(this,R.drawable.main_ui_gradient_green_blue);
        getWindow().setBackgroundDrawable(background);

         */






        mapFragment_instance = new Map_Fragment();
        event_list_parent_holder = new live_events_feed_fragment();
        profile_fragment = new Profile_Fragment();

        if (savedInstanceState == null)
        {
            fragment_transaction =  getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_map_view, mapFragment_instance, null)
                    .add(R.id.fragment_user_profile, profile_fragment,null)
                    .add(R.id.fragment_event_list_parent, event_list_parent_holder,null)
                    .hide(event_list_parent_holder).hide(profile_fragment);
            fragment_transaction.commit();
        }


        /// Initializing DB_Model

        String file_directory = Main_Activity.this.getFilesDir().toString();
        ContextWrapper context_wrapper = new ContextWrapper(getApplicationContext());
        File image_directory = context_wrapper.getDir("Profile_Pictures", Context.MODE_PRIVATE);


        model_db = new ViewModelProvider(this).get(Events_DB_VM.class);
        model_db.set_db_callback(this::update_children_fragments);
        model_db.init_db(file_directory,image_directory);
        Image_Provider.set_file(image_directory);







        /// Initializing and configuring Bottom Navigation
        bottom_navigation_view = findViewById(R.id.bottom_navigation);

        bottom_navigation_view.setOnNavigationItemSelectedListener(item ->
        {
           switch (item.getItemId())
           {

               //assert map_fragment_instance != null;
               case R.id.page_map: //bottom_navigation_view.

                   fragment_transaction = getSupportFragmentManager().beginTransaction();
                   fragment_transaction.show(mapFragment_instance).hide(event_list_parent_holder)
                                                                    .hide(profile_fragment).commit();
                   break;

               case R.id.page_events:
                   fragment_transaction = getSupportFragmentManager().beginTransaction();
                   fragment_transaction.show(event_list_parent_holder).hide(mapFragment_instance)
                                                                        .hide(profile_fragment).commit();
                   break;
               case R.id.page_profile:
                   show_user_profile();
                   break;
           }
            return true;
        });

        ImageButton find_friends_ib = findViewById(R.id.find_friends_ib);
        find_friends_ib.setOnClickListener(v ->
        {
            Intent intent = new Intent(this, Search_Users_Activity.class);
            startActivity(intent);
        });

        /// FireBase
        Fire_Base_Configuring();

    }


    public void show_user_profile()
    {
        fragment_transaction = getSupportFragmentManager().beginTransaction();
        fragment_transaction.show(profile_fragment).hide(event_list_parent_holder)
                .hide(mapFragment_instance).commit();

        bottom_navigation_view.getMenu().getItem(2).setChecked(true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult requestCode: " + requestCode);


        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP)
        {
            assert data != null;
            final Uri resultUri = UCrop.getOutput(data);
            Log.d(TAG,"REQUEST_CROP Uri: "+resultUri);

            image_profile_bitmap = null;

            try
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                {
                    image_profile_bitmap= ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), resultUri));
                }
                else
                    image_profile_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);

                Image_Provider.save_to_internal_storage(image_profile_bitmap, User_Credentials.get_item(Events_DB_VM.USER_PIC));


            }
            catch (IOException | JSONException e){e.printStackTrace();}

            Callback callback = new Callback()
            {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e)
                {
                    Log.d(TAG, "onFailure: " + e);

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
                {
                    assert response.body() != null;
                    Log.d(TAG, "onResponse: " + response.body().string());


                }

            };
            try
            {
                HTTP_Provider.upload_image_profile(image_profile_bitmap,"user_profile_image.jpg",callback);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            Log.d(TAG,"Get REQUEST_CROP: ");
        }
        else if (resultCode == UCrop.RESULT_ERROR)
        {
            final Throwable cropError = UCrop.getError(data);
        }

        if(image_profile_bitmap!=null)
        {
            profile_fragment.user_profile_image_civ.setImageBitmap(image_profile_bitmap);
            refresh_db();
        }




        /*
        Cursor  returnCursor = getContentResolver().query(resultUri, null, null, null, null);
        if (returnCursor != null)
        {
            Log.d(TAG,"returnCursor not Null");
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String name = returnCursor.getString(nameIndex);
            returnCursor.close();
        }
         */
    }

    public void refresh_db()
    {
        model_db.refresh_db();
    }





    void update_children_fragments()
    {
        Log.d(TAG,"Request From Activity to Fragment Sent");

        new Handler(Looper.getMainLooper()).postDelayed(()->
        {
            mapFragment_instance.update_map();
            event_list_parent_holder.refresh_fragments();
            profile_fragment.get_updated();

        }, 500);

    }

    public void bottom_navigation_visibility(boolean visible)
    {
        if(visible)
            bottom_navigation_view.setVisibility(View.VISIBLE);
        else
            bottom_navigation_view.setVisibility(View.GONE);

    }

    private void Fire_Base_Configuring()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            // Create channel to show notifications.
            String channelId  = getString(R.string.bellenet_notification_id);
            String channelName = getString(R.string.bellenet_notification_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }


        /// Get device token

//        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task ->
//        {
//            //String deviceToken = task.getResult();
//            Log.d(TAG, "Device Token: " +  task.getResult());
//        });
//
//
//        if (getIntent().getExtras() != null)
//        {
//            for (String key : getIntent().getExtras().keySet())
//            {
//                Object value = getIntent().getExtras().get(key);
//                Log.d(TAG, "Key: " + key + " Value: " + value);
//            }
//        }
//        // [END handle_data_extras]
//
//        Log.d(TAG, "Subscribing to: "+getString(R.string.bellenet_cloud_messaging_public));
//        // [START subscribe_topics]
//        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.bellenet_cloud_messaging_public))
//                .addOnCompleteListener(task ->
//                {
//                    String msg = "Subscribe Was Successful";
//                    if (!task.isSuccessful())
//                    {
//                        msg = "Subscribe Was Not Successful";
//                    }
//                    Log.d(TAG, msg);
//                    //Toast.makeText(Main_Activity.this, msg, Toast.LENGTH_SHORT).show();
//                });

    }




    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain)
    {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted)
    {
        if (!granted)
        {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }


    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

    }
}