package com.soluk.belle_net_alpha;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mapbox.geojson.Feature;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HTTP_Provider
{

    private static final String BASE_URL = "https://soluk.org/";
    private static final String UPLOAD_URL = "https://soluk.org/belle_net_users_info/upload_profile_pic.php";

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");
    private static final String IMGUR_CLIENT_ID = "...";

    private static final OkHttpClient client = new OkHttpClient();
    private static RequestBody request_body;
    private static Response response;
    private static Request request;
    private static File local_dir;

    public HTTP_Provider()
    {


    }

    public static void post_json(String sub_url, JSONObject json, Callback callback)
    {
        RequestBody body = RequestBody.create(json.toString(),MEDIA_TYPE_JSON);
        String url = BASE_URL+sub_url;
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);

    }

    public static String upload_image_profile(Bitmap bitmap, String name, Callback callback) throws JSONException
    {
        File file = generate_local_image(bitmap,name);
        request_body = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    //.addPart(Headers.of("Content-Disposition", "form-data; name=\"fileToUpload\"; filename=\"user_profile_image.jpg\""),
                                    .addPart(Headers.of("Content-Disposition", "form-data; name=\"fileToUpload\"; filename=\""+
                                                    User_Credentials.get_item("user_pic")+".jpg\""),
                                        RequestBody.create(file, MEDIA_TYPE_JPG))
                                    .build();


        request = new Request.Builder()
                .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .header("user_email",User_Credentials.get_item("user_email"))
                .header("user_password",User_Credentials.get_item("user_password"))
                .url(UPLOAD_URL)
                .post(request_body)
                .build();

        Call call = client.newCall(request);

        call.enqueue(callback);


        return"";
    }


    public static void set_file_dir(File dir) {local_dir = dir;}

    public static File get_file_dir() {return local_dir;}

    private static File generate_local_image (Bitmap bitmap, String name)
    {
        //File filesDir = local_dir;
        if(local_dir==null)
            return null;
        File image_file = new File(local_dir, name + ".jpg");

        OutputStream os;
        try
        {
            os = new FileOutputStream(image_file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        }
        catch (Exception e) { Log.d("HTTP_Handling", "Error writing bitmap", e);}

        return image_file;
    }
}
