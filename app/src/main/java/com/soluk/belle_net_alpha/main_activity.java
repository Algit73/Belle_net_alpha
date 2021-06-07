package com.soluk.belle_net_alpha;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.soluk.belle_net_alpha.main_fragments.Profile_Fragment;
import com.soluk.belle_net_alpha.main_fragments.events_hub_feed_fragment;
import com.soluk.belle_net_alpha.main_fragments.map_fragment;
import com.soluk.belle_net_alpha.model.Events_DB_VM;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;

import okhttp3.Response;


public class main_activity extends AppCompatActivity implements
        PermissionsListener
{

    private static final String TAG = main_activity.class.getSimpleName();


    private FragmentTransaction fragment_transaction;
    private map_fragment map_fragment_instance;
    private events_hub_feed_fragment event_list_parent_holder;
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

    private void request_permission()
    {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)
        {
            // You can use the API that requires the permission.
            Log.d(TAG,"request_permission: Granted");

        }
        //else if (shouldShowRequestPermissionRationale(...))
        //{
        // In an educational UI, explain to the user why your app requires this
        // permission for a specific feature to behave as expected. In this UI,
        // include a "cancel" or "no thanks" button that allows the user to
        // continue using your app without granting the permission.

        //}
        else
        {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            Log.d(TAG,"request_permission: Not Granted");
            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    void request_per_2()
    {
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access ");
            builder.setMessage("Please grant location access so this app can detect devices.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);

        //request_per_2();
        //request_permission();
        //HTTP_Provider.get_file_dir(this.getFilesDir());

        //Picasso.get().load().memoryPolicy()



        map_fragment_instance = new map_fragment();
        event_list_parent_holder = new events_hub_feed_fragment();
        profile_fragment = new Profile_Fragment();

        if (savedInstanceState == null)
        {
            fragment_transaction =  getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_map_view, map_fragment_instance, null)
                    .add(R.id.fragment_user_profile, profile_fragment,null)
                    .add(R.id.fragment_event_list_parent, event_list_parent_holder,null)
                    .hide(event_list_parent_holder).hide(profile_fragment);
            fragment_transaction.commit();
        }


        /// Initializing DB_Model

        String file_directory = main_activity.this.getFilesDir().toString();
        ContextWrapper context_wrapper = new ContextWrapper(getApplicationContext());
        File image_directory = context_wrapper.getDir("Profile_Pictures", Context.MODE_PRIVATE);
        model_db = new ViewModelProvider(this).get(Events_DB_VM.class);
        model_db.set_db_handler(this::update_children_fragments);
        model_db.init_db(file_directory,image_directory);





        /// Initializing and configuring Bottom Navigation
        bottom_navigation_view = findViewById(R.id.bottom_navigation);

        bottom_navigation_view.setOnNavigationItemSelectedListener(item ->
        {
           switch (item.getItemId())
           {

               //assert map_fragment_instance != null;
               case R.id.page_map: //bottom_navigation_view.

                   fragment_transaction = getSupportFragmentManager().beginTransaction();
                   fragment_transaction.show(map_fragment_instance).hide(event_list_parent_holder)
                                                                    .hide(profile_fragment).commit();
                   break;

               case R.id.page_events:
                   fragment_transaction = getSupportFragmentManager().beginTransaction();
                   fragment_transaction.show(event_list_parent_holder).hide(map_fragment_instance)
                                                                        .hide(profile_fragment).commit();
                   break;
               case R.id.page_profile:
                   fragment_transaction = getSupportFragmentManager().beginTransaction();
                   fragment_transaction.show(profile_fragment).hide(event_list_parent_holder)
                                                                .hide(map_fragment_instance).commit();
                   break;
           }
            return true;
        });

        /// FireBase
        Fire_Base_Configuring();
        //check_and_request_permissions();

    }

    private void check_and_request_permissions()
    {
        // Check for permissions

        for (String each_permission : REQUIRED_PERMISSION_LIST)
        {
            if (ContextCompat.checkSelfPermission(this, each_permission) != PackageManager.PERMISSION_GRANTED)
            {
                missingPermission.add(each_permission);
            }
        }
        // Request for missing permissions
        if (missingPermission.isEmpty())
        {

        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //showToast("Need to grant the permissions!");
            Toast.makeText(getApplicationContext(),"Need to grant the permissions!",
                    Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    missingPermission.toArray(new String[missingPermission.size()]),
                    REQUEST_PERMISSION_CODE);
        }

    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check for granted permission and remove from missing list
        if (requestCode == REQUEST_PERMISSION_CODE)
        {
            for (int i = grantResults.length - 1; i >= 0; i--)
            {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                {
                    missingPermission.remove(permissions[i]);
                }
            }
        }
        // If there is enough permission, we will start the registration
        if (missingPermission.isEmpty())
        {

        }
        else
        {
            //showToast("Missing permissions!!!");
            Toast.makeText(getApplicationContext(),"Need to grant the permissions!",
                    Toast.LENGTH_SHORT).show();
        }
    }

     */



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "onActivityResult requestCode: " + requestCode);

        if (requestCode== LocationRequest.PRIORITY_HIGH_ACCURACY||requestCode==LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                ||requestCode== LocationRequest.PRIORITY_LOW_POWER)
        {
            Log.d(TAG, "onActivityResult: GPS asked");
            switch (resultCode)
            {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    Log.d(TAG, "onActivityResult: GPS Enabled by user");
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    Log.i(TAG, "onActivityResult: User rejected GPS request");
                    main_activity.this.finish();
                    break;
                default:
                    break;
            }
        }

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

                //saveToInternalStorage(image_profile_bitmap, User_Credentials.get_item("user_pic"));
                Events_DB_VM.save_to_internal_storage(image_profile_bitmap, User_Credentials.get_item("user_pic"));


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

    private String saveToInternalStorage(Bitmap bitmapImage, String name)
    {
        File path=new File(Events_DB_VM.get_image_file(),name);
        Log.d(TAG,"Profile pic address "+path);

        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(path);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        }
        catch (Exception e)
        {
            Log.d(TAG,"Int Storage Create File failed: "+e.getMessage());
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
        Log.d(TAG,"Directory: "+ Events_DB_VM.get_image_file().getAbsolutePath());
        return Events_DB_VM.get_image_file().getAbsolutePath();
    }



    void update_children_fragments()
    {
        Log.d(TAG,"Request From Activity to Fragment Sent");

        new Handler(Looper.getMainLooper()).postDelayed(()->
        {
            map_fragment_instance.update_map();
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
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task ->
        {
            //String deviceToken = task.getResult();
            Log.d(TAG, "Device Token: " +  task.getResult());
        });


        if (getIntent().getExtras() != null)
        {
            for (String key : getIntent().getExtras().keySet())
            {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

        Log.d(TAG, "Subscribing to: "+getString(R.string.bellenet_cloud_messaging_public));
        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.bellenet_cloud_messaging_public))
                .addOnCompleteListener(task ->
                {
                    String msg = "Subscribe Was Successful";
                    if (!task.isSuccessful())
                    {
                        msg = "Subscribe Was Not Successful";
                    }
                    Log.d(TAG, msg);
                    Toast.makeText(main_activity.this, msg, Toast.LENGTH_SHORT).show();
                });

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