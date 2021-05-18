package com.soluk.belle_net_alpha;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.soluk.belle_net_alpha.model.event_db_vm;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

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
    private event_list_fragment_parent event_list_parent_holder;
    private Profile_Fragment profile_fragment;

    private BottomNavigationView bottom_navigation_view;

    private Bitmap image_profile_bitmap;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);
        HTTP_Provider.get_file_dir(this.getFilesDir());



        map_fragment_instance = new map_fragment();
        event_list_parent_holder = new event_list_fragment_parent();
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
        event_db_vm model_db = new ViewModelProvider(this).get(event_db_vm.class);
        model_db.set_db_handler(this::update_map_fragment);
        model_db.init_db(file_directory,image_directory);



        /// Registering MapBox




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





    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

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
            }
            catch (IOException e){e.printStackTrace();}

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
            HTTP_Provider.upload_image_profile(image_profile_bitmap,"test1.jpg",callback);

            Log.d(TAG,"Get REQUEST_CROP: ");
        }
        else if (resultCode == UCrop.RESULT_ERROR)
        {
            final Throwable cropError = UCrop.getError(data);
        }

        if(image_profile_bitmap!=null)
            profile_fragment.user_profile_image_civ.setImageBitmap(image_profile_bitmap);


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



    void update_map_fragment()
    {
        Log.d(TAG,"Request From Activity to Fragment Sent");

        new Handler(Looper.getMainLooper()).postDelayed(()->
        {
            map_fragment_instance.update_map();
            event_list_parent_holder.refresh_fragments();

        }, 500);

    }

    void bottom_navigation_visibility(boolean visible)
    {
        if(visible)
            bottom_navigation_view.setVisibility(View.VISIBLE);
        else
            bottom_navigation_view.setVisibility(View.GONE);

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