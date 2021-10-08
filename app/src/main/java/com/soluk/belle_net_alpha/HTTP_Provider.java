package com.soluk.belle_net_alpha;

import android.graphics.Bitmap;
import android.util.Log;

import com.soluk.belle_net_alpha.model.Events_DB_VM;
import com.soluk.belle_net_alpha.selected_event.Selected_Event_Info_Fragment;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_DATE;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_CREATION_DATE;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_DATE_END;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_NAME;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_TYPE;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_PIC;

public class HTTP_Provider
{

    private static final String BASE_URL = "https://soluk.org/";
    private static final String UPLOAD_URL = "https://soluk.org/belle_net_users_info/upload_profile_pic.php";
    private static final String UPLOAD_TEXT = "https://soluk.org/belle_net_users_info/events_descriptions.php";

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
    private static final MediaType MEDIA_TYPE_TXT = MediaType.parse("text/plain");
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

    public static String upload_text(JSONObject json, String body, Callback callback) throws JSONException
    {
        Iterator<?> keys = json.keys();

        while( keys.hasNext() )
        {
            String key = (String) keys.next();
            String TAG = Selected_Event_Info_Fragment.class.getSimpleName();
            Log.d(TAG, "set: "+key);
        }
        Log.d("map_fragment", "upload_text: Received JSON: "+json);


        //OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                //.addFormDataPart("file", file.getName(),
                  //      RequestBody.create("asasas",MediaType.parse("text/plain")))
                //.addFormDataPart("other_field", "other_field_value")
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"fileToUpload\"; filename=\""
                                + User_Credentials.get_item(Events_DB_VM.USER_ID)
                                + "_des.txt\""),
                        RequestBody.create(body, MEDIA_TYPE_TXT))
                .build();
        Request request = new Request.Builder()
                .header(Events_DB_VM.USER_NAME,json.get(Events_DB_VM.USER_NAME).toString())
                .header(Events_DB_VM.USER_FAMILY,json.get(Events_DB_VM.USER_FAMILY).toString())
                .header(Events_DB_VM.USER_ID,json.get(Events_DB_VM.USER_ID).toString())
                .header(Events_DB_VM.USER_EMAIL,json.get(Events_DB_VM.USER_EMAIL).toString())
                .header(Events_DB_VM.USER_PASSWORD,json.get(Events_DB_VM.USER_PASSWORD).toString())
                .header(EVENT_NAME,json.get(EVENT_NAME).toString())
                .header(EVENT_TYPE,json.get(EVENT_TYPE).toString())
                .header(EVENT_DATE, json.get(EVENT_DATE).toString())
                .header(EVENT_DATE_END, json.get(EVENT_DATE_END).toString())
                .header(Events_DB_VM.EVENT_TIME,json.get(Events_DB_VM.EVENT_TIME).toString())
                .header(Events_DB_VM.EVENT_TIME_END,json.get(Events_DB_VM.EVENT_TIME_END).toString())
                .header(Events_DB_VM.USER_TYPE,json.get(Events_DB_VM.USER_TYPE).toString())
                .header(USER_PIC,json.get(USER_PIC).toString())
                .header("longitude_0",json.get("longitude_0").toString())
                .header("longitude_1",json.get("longitude_1").toString())
                .header("latitude_0",json.get("latitude_0").toString())
                .header("latitude_1",json.get("latitude_1").toString())

                .url(UPLOAD_TEXT)
                .post(formBody).build();



        Call call = client.newCall(request);
        call.enqueue(callback);



        return "";
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
