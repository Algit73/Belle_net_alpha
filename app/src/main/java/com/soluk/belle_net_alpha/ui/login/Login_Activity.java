package com.soluk.belle_net_alpha.ui.login;

import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.soluk.belle_net_alpha.HTTP_Provider;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.Main_Activity;
import com.soluk.belle_net_alpha.model.Events_DB_VM;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_EMAIL;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_FAMILY;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_NAME;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_PASSWORD;

public class Login_Activity extends AppCompatActivity
{


    private static final String TAG = Login_Activity.class.getSimpleName();
    private static final String LOGIN_SUBURL = "belle_net_users_info/user_login_check.php";
    private final String USER_CREDENTIALS = "user_cred";


    private final String USER_REQUEST = "user_request";
    private final String SIGN_IN_REQUEST = "signin";
    private final String SIGN_UP_REQUEST = "signup";

    ImageView loading_screen_1;
    ImageView loading_screen_2;
    ImageView loading_screen_3;
    ProgressBar loading_wheel;

    ConstraintLayout sign_up_card_cl;
    ConstraintLayout sign_in_card_cl;


    private EditText user_name_et;
    private EditText user_family_et;
    private EditText user_email_et;
    private EditText user_pass_et;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController controller = getWindow().getInsetsController();

            if (controller != null)
                controller.hide(WindowInsets.Type.systemBars());
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            window.setDecorFitsSystemWindows(false)
        }
        else
        {
            //noinspection deprecation
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }

         */

        /*WindowInsetsController(,)

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);*/

        /// Giving the http_provider a main address to store data
        File file_directory = Login_Activity.this.getFilesDir();
        HTTP_Provider.set_file_dir(file_directory);

        /// Setting up the GUI elements
        /// Loading Screens are low transparent screens with gray colors
        loading_screen_1 = findViewById(R.id.loading_screen_1);
        loading_screen_2 = findViewById(R.id.loading_screen_2);
        loading_screen_3 = findViewById(R.id.loading_screen_3);
        loading_wheel = findViewById(R.id.loading_wheel);

        sign_up_card_cl = findViewById(R.id.sign_up_card_cl);
        sign_in_card_cl = findViewById(R.id.sign_in_card_cl);


        User_Credentials.init(file_directory.toString(),USER_CREDENTIALS);


        final Button login_button = findViewById(R.id.login);
        final EditText username_et = findViewById(R.id.username);
        final EditText password_et = findViewById(R.id.password);
        login_button.setEnabled(true);
        login_button.setOnClickListener(v->
        {
            if((username_et.getText()!=null)&&(password_et.getText()!=null))
            {
                try
                {
                    User_Credentials.put_user_item(USER_EMAIL,username_et.getText().toString());
                    User_Credentials.put_user_item(USER_PASSWORD,password_et.getText().toString());
                    User_Credentials.write_user_items();
                    send_user_creds();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            else
                Toast.makeText(this,"Please fill user and pass",Toast.LENGTH_LONG).show();
        });


        try
        {
            Log.d("Login_Activity","user_creds: "+User_Credentials.get_items());
            username_et.setText(User_Credentials.get_item(USER_EMAIL));
            password_et.setText(User_Credentials.get_item(USER_PASSWORD));
        }
        catch (JSONException e) {e.printStackTrace();}

        ///
        user_name_et = findViewById(R.id.user_name_su_et);
        user_family_et = findViewById(R.id.user_family_su_et);
        user_email_et = findViewById(R.id.user_email_su_et);
        user_pass_et = findViewById(R.id.user_password_su_et);
        Button register_user_btn = findViewById(R.id.register_btn);
        EditText user_pass_confirm_et = findViewById(R.id.user_password_confirm_su);
        register_user_btn.setOnClickListener(v->
        {

            if(user_name_et.getText().toString().matches(""))
            {
                Toast.makeText(this,"Please fill the name",Toast.LENGTH_LONG).show();
                return;
            }
            else if(user_family_et.getText().toString().matches(""))
            {
                Toast.makeText(this,"Please fill the family",Toast.LENGTH_LONG).show();
                return;
            }
            else if(user_email_et.getText().toString().matches(""))
            {
                Toast.makeText(this,"Please fill the email",Toast.LENGTH_LONG).show();
                return;
            }
            else if(user_pass_et.getText().toString().matches(""))
            {
                Toast.makeText(this,"Please fill the password",Toast.LENGTH_LONG).show();
                return;
            }
            else if(user_pass_confirm_et.getText().toString().matches(""))
            {
                Toast.makeText(this,"Please fill the confirm password",Toast.LENGTH_LONG).show();
                return;
            }
            else if(!user_pass_et.getText().toString().matches(user_pass_confirm_et.getText().toString()))
            {
                Toast.makeText(this,"Password does not match",Toast.LENGTH_LONG).show();
                Log.d(TAG,"Pass: "+user_pass_et.getText());
                Log.d(TAG,"Pass Conf: "+user_pass_confirm_et.getText());
                return;
            }

            try { send_user_register_request();}
            catch (JSONException e) {e.printStackTrace();}

        });

        TextView sign_up_tv = findViewById(R.id.sign_up_tv);
        TextView sign_in_tv = findViewById(R.id.sign_in_tv);

        sign_up_tv.setOnClickListener(v->
        {
            sign_up_card_cl.setVisibility(View.GONE);
            sign_in_card_cl.setVisibility(View.VISIBLE);
        });

        sign_in_tv.setOnClickListener(v->
        {
            sign_in_card_cl.setVisibility(View.GONE);
            sign_up_card_cl.setVisibility(View.VISIBLE);
        });


        try
        {
            User_Credentials.put_user_item(USER_EMAIL,username_et.getText().toString());
            User_Credentials.put_user_item(USER_PASSWORD,password_et.getText().toString());
            User_Credentials.write_user_items();
            send_user_creds();
        } catch (Exception ignored){}

    }



    void send_user_creds()
    {
        loading_screen_1.setVisibility(View.VISIBLE);
        loading_screen_2.setVisibility(View.VISIBLE);
        loading_wheel.setVisibility(View.VISIBLE);

        //user_creds = credentials_file.read_json();
        Log.d("Login_Activity","user_creds: "+User_Credentials.get_items());

        Callback callback = new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                Log.d("Login_Activity_login","onFailure "+ e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                String response_body = response.body().string();
                Handler mainHandler = new Handler(getMainLooper());
                Runnable myRunnable = () -> loading_ui_handling("");
                mainHandler.post(myRunnable);
                Log.d("Login_Activity","response_body: "+response_body);
                JSONObject user_response = null;
                try
                {
                    user_response = new JSONObject(response_body);

                    if(user_response.get("eligible").toString().equals("eligible"))
                    {

                        User_Credentials.put_user_item(USER_NAME,user_response.get(USER_NAME).toString());
                        User_Credentials.put_user_item(USER_FAMILY,user_response.get(USER_FAMILY).toString());
                        User_Credentials.put_user_item(Events_DB_VM.USER_ID,user_response.get(Events_DB_VM.USER_ID).toString());
                        User_Credentials.put_user_item(Events_DB_VM.USER_PIC,user_response.get(Events_DB_VM.USER_PIC).toString());
                        User_Credentials.put_user_item(Events_DB_VM.USER_JOIN_DATE,user_response.get(Events_DB_VM.USER_JOIN_DATE).toString());
                        User_Credentials.write_user_items();
                        Intent home = new Intent(Login_Activity.this,
                                Main_Activity.class);
                        startActivity(home);
                        Login_Activity.this.finish();
                    }
                    else
                        Toast.makeText(Login_Activity.this,"Username or Password is wrong",Toast.LENGTH_LONG).show();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }


            }
        };

        JSONObject json = new JSONObject();
        try
        {
            json.put(USER_REQUEST, SIGN_IN_REQUEST);
            json.put(USER_PASSWORD, User_Credentials.get_item(USER_PASSWORD));
            json.put(USER_EMAIL, User_Credentials.get_item(USER_EMAIL));
        }
        catch (JSONException e) {e.printStackTrace();}
        HTTP_Provider.post_json(LOGIN_SUBURL,json,callback);

    }

    void send_user_register_request() throws JSONException
    {

        JSONObject json = new JSONObject();
        json.put(USER_REQUEST,SIGN_UP_REQUEST);
        json.put(USER_NAME,user_name_et.getText().toString());
        json.put(USER_FAMILY,user_family_et.getText().toString());
        json.put(USER_EMAIL,user_email_et.getText().toString());
        json.put(USER_PASSWORD, user_pass_et.getText().toString());
        Log.d("Login_Activity","json "+json);

        Callback callback = new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                Log.d("Login_Activity_register","onFailure "+ e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                String response_body = response.body().string();
                Log.d("Login_Activity","response_body: "+response_body);
                Handler mainHandler = new Handler(getMainLooper());
                Runnable myRunnable = () -> loading_ui_handling(response_body);
                mainHandler.post(myRunnable);

                if(response_body.equals("EX"))
                    Toast.makeText(getApplication(), "Email Exists", Toast.LENGTH_LONG).show();

            }
        };

        HTTP_Provider.post_json(LOGIN_SUBURL,json,callback);


    }
    private void loading_ui_handling(String response)
    {
        loading_screen_1.setVisibility(View.GONE);
        loading_screen_2.setVisibility(View.GONE);
        loading_screen_3.setVisibility(View.GONE);
        loading_wheel.setVisibility(View.GONE);

        if(response.equals("Granted"))
        {
            sign_up_card_cl.setVisibility(View.GONE);
            sign_in_card_cl.setVisibility(View.VISIBLE);
        }

    }

}