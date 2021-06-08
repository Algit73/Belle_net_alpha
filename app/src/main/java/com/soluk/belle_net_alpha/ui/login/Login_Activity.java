package com.soluk.belle_net_alpha.ui.login;

import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.soluk.belle_net_alpha.HTTP_Provider;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.event_data_maker.file_maker;
import com.soluk.belle_net_alpha.main_activity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Login_Activity extends AppCompatActivity
{


    private static final String TAG = Login_Activity.class.getSimpleName();
    private static final String LOGIN_SUBURL = "belle_net_users_info/user_login_check.php";
    private final String USER_CREDENTIALS = "user_cred";
    private final String USER_EMAIL = "user_email";
    private final String USER_NAME = "user_name";
    private final String USER_FAMILY = "user_family";
    private final String USER_PASS = "user_password";
    private final String USER_REQUEST = "user_request";
    private final String SIGN_IN_REQUEST = "signin";
    private final String SIGN_UP_REQUEST = "signup";
    //private JSONObject user_creds;
    //file_maker credentials_file;
    ImageView loading_screen_1;
    ImageView loading_screen_2;
    ImageView loading_screen_3;
    ProgressBar loading_wheel;

    CardView card_view_sign_up;
    CardView card_view_sign_in;


    private EditText user_name_et;
    private EditText user_family_et;
    private EditText user_email_et;
    private EditText user_pass_et;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /// Giving the http_provider a main address to store data
        File file_directory = Login_Activity.this.getFilesDir();
        HTTP_Provider.set_file_dir(file_directory);


        //loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
        //        .get(LoginViewModel.class);

        /// Setting up the GUI elements
        loading_screen_1 = findViewById(R.id.loading_screen_1);
        loading_screen_2 = findViewById(R.id.loading_screen_2);
        loading_screen_3 = findViewById(R.id.loading_screen_3);
        loading_wheel = findViewById(R.id.loading_wheel);

        card_view_sign_up = findViewById(R.id.sign_up_card);
        card_view_sign_in = findViewById(R.id.sign_in_card);


        User_Credentials.init(file_directory.toString(),USER_CREDENTIALS);
        //credentials_file = new file_maker(file_directory.toString(),USER_CREDENTIALS);
        //user_creds = credentials_file.read_json();



        final Button login_button = findViewById(R.id.login);
        final EditText username_et = findViewById(R.id.username);
        final EditText password_et = findViewById(R.id.password);
        login_button.setEnabled(true);
        login_button.setOnClickListener(v->
        {
            if((username_et.getText()!=null)&&(password_et.getText()!=null))
            {
                //JSONObject user_pass = new JSONObject();
                try
                {
                    //user_pass.put(USER_EMAIL,username_et.getText())
                      //      .put(USER_PASS,password_et.getText());
                    User_Credentials.put_user_item(USER_EMAIL,username_et.getText().toString());
                    User_Credentials.put_user_item(USER_PASS,password_et.getText().toString());
                    User_Credentials.write_user_items();
                    //credentials_file.write_json(user_pass);
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
            //username_et.setText(user_creds.get(USER_EMAIL).toString());
            //password_et.setText(user_creds.get(USER_PASS).toString());
            username_et.setText(User_Credentials.get_item(USER_EMAIL));
            password_et.setText(User_Credentials.get_item(USER_PASS));
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
            card_view_sign_up.setVisibility(View.GONE);
            card_view_sign_in.setVisibility(View.VISIBLE);
        });

        sign_in_tv.setOnClickListener(v->
        {
            card_view_sign_in.setVisibility(View.GONE);
            card_view_sign_up.setVisibility(View.VISIBLE);
        });

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
                    //user_response.put(USER_PASS,user_creds.get(USER_PASS).toString());
                    //user_response.put(USER_EMAIL,user_creds.get(USER_EMAIL).toString());

                    if(user_response.get("eligible").toString().equals("eligible"))
                    {
                        //credentials_file.write_json(user_response);
                        User_Credentials.put_user_item("user_name",user_response.get("user_name").toString());
                        User_Credentials.put_user_item("user_family",user_response.get("user_family").toString());
                        User_Credentials.put_user_item("user_id",user_response.get("user_id").toString());
                        User_Credentials.put_user_item("user_pic",user_response.get("user_pic").toString());
                        User_Credentials.put_user_item("user_join_date",user_response.get("user_join_date").toString());
                        User_Credentials.write_user_items();
                        Intent home = new Intent(Login_Activity.this,
                                main_activity.class);
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
            json.put(USER_PASS, User_Credentials.get_item(USER_PASS));
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
        json.put(USER_PASS, user_pass_et.getText().toString());
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
            card_view_sign_up.setVisibility(View.GONE);
            card_view_sign_in.setVisibility(View.VISIBLE);
        }

    }

}