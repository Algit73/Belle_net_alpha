package com.soluk.belle_net_alpha.main_fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.soluk.belle_net_alpha.Date_Time_Provider;
import com.soluk.belle_net_alpha.HTTP_Provider;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.event_data_maker.file_maker;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;
import com.soluk.belle_net_alpha.user_followx.User_FollowX_List_Activity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.yalantis.ucrop.UCrop;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static android.os.Looper.getMainLooper;


public class Profile_Fragment extends Fragment
{
    private static final String TAG = Profile_Fragment.class.getSimpleName();



    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private List<String> missingPermission = new ArrayList<>();
    private static final int REQUEST_PERMISSION_CODE = 731;



    private boolean is_permission_checked;
    private int IMAGE_CHOOSE = 1000;
    private int PERMISSION_CODE = 1001;
    private final String USER_CREDENTIALS = "user_cred";
    private static final String REQUEST_DB_USER_ID_SUB_URL = "belle_net_users_info/return_user_id.php";
    private static final String REQUEST_DB_USER_FOLLOWERS_SUB_URL = "belle_net_users_info/return_followings_followers_request.php";
    private TextView user_name_family_tv;
    private TextView user_join_date_tv;
    private TextView user_followers_tv;
    private TextView user_followings_tv;





    public ImageView iv;
    public CircleImageView user_profile_image_civ;



    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri ->
            {
                Log.d(TAG,"Result Received");

                ContextWrapper context_wrapper = new ContextWrapper(getContext());
                File directory = context_wrapper.getDir("Profile_Pictures", Context.MODE_PRIVATE);
                File output_dir=new File(directory,"selected_image.jpg");

                if (null != uri)
                    UCrop.of(uri, Uri.fromFile(output_dir))
                            .withAspectRatio(16, 9)
                            .withMaxResultSize(200, 200)
                            .start(getActivity());

            });


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->
            {
                Log.d(TAG,"ActivityResultLauncher Activated");
                if (result.getResultCode() == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
                {
                    CropImage.ActivityResult image_result = CropImage.getActivityResult(result.getData());
                    if (result.getResultCode() == RESULT_OK)
                    {
                        Uri resultUri = image_result.getUri();
                        iv.setImageURI(resultUri);
                    }
                }

                /*
                ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                if (result.getResultCode() == Activity.RESULT_OK) {
                                    Intent intent = result.getIntent();
                                    // Handle the Intent
                                }
                            }
                        });

                 */

            });



    public Profile_Fragment()
    {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        check_and_request_permissions();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        user_name_family_tv = v.findViewById(R.id.user_name_family_tv);
        user_join_date_tv = v.findViewById(R.id.user_join_date_num_tv);
        user_followers_tv = v.findViewById(R.id.user_followers_num_tv);
        user_followings_tv = v.findViewById(R.id.user_followings_num_tv);

        ConstraintLayout followers_holder = v.findViewById(R.id.followers_holder);
        ConstraintLayout followings_holder = v.findViewById(R.id.followings_holder);

        user_profile_image_civ = v.findViewById(R.id.user_profile_image_civ);
        ImageButton add_user_profile_image_ib = v.findViewById(R.id.add_user_profile_image);
        add_user_profile_image_ib.setOnClickListener(c->
        {
            if(is_permission_checked);
                mGetContent.launch("image/*");
        });

        followings_holder.setOnClickListener(v1 ->
        {
            initiate_followx_page (0);
        });

        followers_holder.setOnClickListener(v1 ->
        {
            initiate_followx_page (1);
        });


        return v;
    }

    void initiate_followx_page (int mode)
    {
        Callback callback = new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                String body = response.body().string();
                Log.d(TAG,"Send onResponse: "+ body);

                Intent intent = new Intent(getActivity(), User_FollowX_List_Activity.class);
                intent.putExtra("body", body);
                intent.putExtra("mode", mode);
                startActivity(intent);
            }
        };

        JSONObject user_follow = new JSONObject();
        try
        {
            user_follow.put("user_id", User_Credentials.get_item("user_id"));
            user_follow.put("user_email",User_Credentials.get_item("user_email"));
            user_follow.put("user_password",User_Credentials.get_item("user_password"));
            user_follow.put("user_request","followx");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        HTTP_Provider.post_json(REQUEST_DB_USER_FOLLOWERS_SUB_URL,user_follow,callback);
    }

    private void update_followx_stat_tvs(JSONObject followx_stat)
    {
        Handler mainHandler = new Handler(getMainLooper());
        Runnable myRunnable = () ->
        {
            try
            {
                user_followers_tv.setText(followx_stat.get("followers").toString());
                user_followings_tv.setText(followx_stat.get("followings").toString());
            }
            catch (JSONException e) {e.printStackTrace();}

        };
        mainHandler.post(myRunnable);
    }

    private void update_followx_stat()
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
                String body = response.body().string();

                try
                {
                    JSONObject followx_stat = new JSONObject(body);
                    update_followx_stat_tvs(followx_stat);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                Log.d(TAG,"Send onResponse: "+ body);

            }
        };

        JSONObject user_follow = new JSONObject();
        try
        {
            user_follow.put("user_id", User_Credentials.get_item("user_id"));
            user_follow.put("user_email",User_Credentials.get_item("user_email"));
            user_follow.put("user_password",User_Credentials.get_item("user_password"));
            user_follow.put("user_request","numbers");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


        HTTP_Provider.post_json(REQUEST_DB_USER_FOLLOWERS_SUB_URL,user_follow,callback);
    }

    public void get_updated()
    {
        try {request_user_id(get_user_id());} catch (Exception ignored){}
        update_followx_stat();
    }

    private JSONObject get_user_id() throws JSONException
    {
        file_maker credentials_file = new file_maker(HTTP_Provider.get_file_dir().toString(),USER_CREDENTIALS);
        JSONObject user_creds = credentials_file.read_json();
        return user_creds;

    }

    private void request_user_id(JSONObject user_cred)
    {
        Callback callback = new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                Log.d(TAG,"User ID onFailure: "+ e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                String body = response.body().string();
                try
                {
                    JSONObject user_profile = new JSONObject(body);
                    update_user_profile(user_profile);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                Log.d(TAG,"User ID onResponse: "+body);
            }
        };
        HTTP_Provider.post_json(REQUEST_DB_USER_ID_SUB_URL,user_cred,callback);

    }

    private void update_user_profile(JSONObject profile)
    {


        Handler mainHandler = new Handler(getMainLooper());
        Runnable myRunnable = () ->
        {
            try
            {
                String user_name_family = profile.get("user_name")+" "+profile.get("user_family");
                user_name_family_tv.setText(user_name_family);
                user_join_date_tv.setText(Date_Time_Provider
                                            .date_reformat(profile.get("user_join_date").toString()));
                user_profile_image_civ.setImageBitmap(get_profile_bmp(profile.get("user_pic").toString()));

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        };
        mainHandler.post(myRunnable);

    }

    private Bitmap get_profile_bmp(String name)
    {
        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("Profile_Pictures", Context.MODE_PRIVATE);
        try
        {
            File file =new File(directory, name);
            return BitmapFactory.decodeStream(new FileInputStream(file));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }




    private void check_and_request_permissions()
    {
        // Check for permissions

        for (String each_permission : REQUIRED_PERMISSION_LIST)
        {
            if (getContext().checkSelfPermission(each_permission) != PackageManager.PERMISSION_GRANTED)
            {
                missingPermission.add(each_permission);
            }
        }
        // Request for missing permissions
        if (missingPermission.isEmpty())
        {
            is_permission_checked = true;
            Log.d(TAG,"Permission Granted");

        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //showToast("Need to grant the permissions!");
            is_permission_checked = false;
            Toast.makeText(getContext(),"Need to grant the permissions!",
                    Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(),
                    missingPermission.toArray(new String[missingPermission.size()]),
                    REQUEST_PERMISSION_CODE);
        }

    }

    private void showImagePicDialog() {
        String options[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromGallery();
                    }
                } else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }

                 */

            }
        });
        builder.create().show();
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Picasso.with(this).load(resultUri).into(userpic);
            }
        }
    }

     */


}