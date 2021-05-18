package com.soluk.belle_net_alpha;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.soluk.belle_net_alpha.model.event_db_vm;
import com.soluk.belle_net_alpha.ui.login.Login_Activity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.yalantis.ucrop.UCrop;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile_Fragment extends Fragment
{
    private static final String TAG = Profile_Fragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private List<String> missingPermission = new ArrayList<>();
    private static final int REQUEST_PERMISSION_CODE = 731;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean is_permission_checked;
    private int IMAGE_CHOOSE = 1000;
    private int PERMISSION_CODE = 1001;
    //CropImageView mCropView;


    public ImageView iv;
    public CircleImageView user_profile_image_civ;



    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>()
            {
                @Override
                public void onActivityResult(Uri uri)
                {
                    Log.d(TAG,"Result Received");

                    ContextWrapper context_wrapper = new ContextWrapper(getContext());
                    File directory = context_wrapper.getDir("Profile_Pictures", Context.MODE_PRIVATE);
                    File output_dir=new File(directory,"selected_image.jpg");

                    if (null != uri)
                        UCrop.of(uri, Uri.fromFile(output_dir))
                                .withAspectRatio(16, 9)
                                .withMaxResultSize(100, 100)
                                .start(getActivity());

                }
            });


    public static String uploadImage(String url, String imagePath) throws IOException, JSONException
    {
        OkHttpClient okHttpClient = new OkHttpClient();
        Log.d("imagePath", imagePath);
        File file = new File(imagePath);
        RequestBody image = RequestBody.create(MediaType.parse("image/jpg"), file);
        Log.d(TAG,"uploadImage: "+image.toString());


        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", imagePath, image)
                .build();
        Log.d(TAG,"requestBody: "+requestBody.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Log.d(TAG,"request: "+request.toString());
        Response response = okHttpClient.newCall(request).execute();
        //JSONObject jsonObject = new JSONObject(response.body().string());


        /*
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e)
            {
                //listener.uploadFailed(e.getMessage());
                Log.d(TAG,"OKHTTP onFailure");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)
            {
                Log.d(TAG,"OKHTTP onResponse "+response);
                //if (response.isSuccessful())
                  //  listener.uploadSuccess();
            }
        });
        //return jsonObject.optString("image");
        return call.toString();

         */
        return "";//response.body().string();
    }


    private void uploadImageToServer(Bitmap bitmap)
    {
        File imageFile = persistImage(bitmap, "profile");
        Ion.with(getActivity())
                .load("POST",getString(R.string.profile_pic_url))  // replace it with your upload url
                .setMultipartFile("profile_pic", "image/jpeg", imageFile)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result)
                    {
                        Log.d(TAG, "Ion Completed " + result);
                    }
                });
    }

    private File persistImage(Bitmap bitmap, String name)
    {
        File filesDir = getContext().getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.d(TAG, "Error writing bitmap", e);
        }

        return imageFile;
    }



/*
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->
            {



                if (result.getResultCode() == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
                {
                    // There are no request codes
                    Uri resultUri = result.getUri();
                    //Intent data = result.getData();
                    //doSomeOperations();
                }
            });

 */


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

                /*
                if (result.getResultCode() == RESULT_OK)
                {
                    // There are no request codes
                    Intent data = result.getData();
                    //doSomeOperations();
                }

                 */
            });



    public Profile_Fragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile_Fragment newInstance(String param1, String param2)
    {
        Profile_Fragment fragment = new Profile_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        check_and_request_permissions();
        //Intent intent = new Intent(String.valueOf(this));
        //someActivityResultLauncher.launch(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        //mCropView = v.findViewById(R.id.cropImageView);



        user_profile_image_civ = v.findViewById(R.id.user_profile_image);
        ImageButton add_user_profile_image_ib = v.findViewById(R.id.add_user_profile_image);
        add_user_profile_image_ib.setOnClickListener(c->
        {

            if(is_permission_checked);
            //    chooseImageGallery();
                mGetContent.launch("image/*");

        });

        return v;
    }

    private void chooseImageGallery()
    {


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