package com.soluk.belle_net_alpha.selected_event;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Feature;
import com.soluk.belle_net_alpha.HTTP_Provider;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.utils.Post_Request_Creator;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.os.Looper.getMainLooper;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.COMMENT_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.COMMENT_REPLIES;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_COMMENT;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_REQUEST;

public class Selected_Event_Comment_Replies_Activity extends AppCompatActivity
{
    private FragmentTransaction fragment_transaction;
    private Selected_Event_Comment_Replies_Fragment selected_event_comment_replies_fragment;
    private final String TAG = Selected_Event_Comment_Replies_Fragment.class.getSimpleName();
    private FloatingActionButton add_comment_fab;
    private LinearLayout user_comment_holder_ll;
    private EditText user_comment_et;
    private ImageButton send_comment_ib;
    JSONObject extra = null;
    private String comment_id;
    private String event_id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_event_comment_replies);

        Intent intent = getIntent();

        Log.d(TAG,intent.getStringExtra(COMMENT_ID) +"\n");

        comment_id = intent.getStringExtra(COMMENT_ID);
        event_id = intent.getStringExtra(EVENT_ID);
        selected_event_comment_replies_fragment = Selected_Event_Comment_Replies_Fragment
                                                    .newInstance(comment_id);


        if (savedInstanceState == null)
        {
            fragment_transaction =  getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_comment_replies, selected_event_comment_replies_fragment, null);
            fragment_transaction.commit();
//            fragment_transaction.
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */)
        {
            @Override
            public void handleOnBackPressed()
            {
                Toast.makeText(getApplicationContext(),"Mahshid e Jan",Toast.LENGTH_SHORT).show();
                if(add_comment_fab.getVisibility()==View.GONE)
                {
                    add_comment_fab.setVisibility(View.VISIBLE);
                    user_comment_holder_ll.setVisibility(View.GONE);
                }
                else
                    Selected_Event_Comment_Replies_Activity.this.finish();

//                super.onBackPressed();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        add_comment_fab = findViewById(R.id.add_comment_fab);
        add_comment_fab.setOnClickListener(v ->
        {
            add_comment_fab.setVisibility(View.GONE);
            user_comment_holder_ll.setVisibility(View.VISIBLE);
        });

        user_comment_holder_ll = findViewById(R.id.user_comment_holder_ll);

        send_comment_ib = findViewById(R.id.send_comment_ib);
        user_comment_et = findViewById(R.id.user_comment_et);

        user_comment_et.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if  (!user_comment_et.getText().toString().isEmpty())
                {
                    send_comment_ib.setEnabled(true);
                    send_comment_ib.setImageTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(getApplicationContext(), R.color.palette_teal_light)));

                }
                else
                {
                    send_comment_ib.setEnabled(false);
                    send_comment_ib.setImageTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(getApplicationContext(), R.color.palette_gray_semi_dark)));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        send_comment_ib.setEnabled(false);
        send_comment_ib.setOnClickListener(v ->
        {
            Toast.makeText(getApplicationContext(),"Mahshid e Janam",Toast.LENGTH_SHORT).show();
            try
            {
                Post_Request_Creator request = new Post_Request_Creator();
                request.add(EVENT_ID,event_id);
                request.add(COMMENT_ID,comment_id);
                request.add(USER_REQUEST,"event_add_comment_reply");
                request.add(USER_COMMENT,user_comment_et.getText().toString().replace("'","\\'"));
                send_post_request(request);

            } catch (Exception e){e.printStackTrace();}
        } );

        ImageButton back_button_ib = findViewById(R.id.back_button_ib);
        back_button_ib.setOnClickListener(v ->
        {
            super.onBackPressed();
        });



    }

    public static void hideKeyboard(Activity activity)
    {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void send_post_request(Post_Request_Creator request)
    {
        Callback callback = new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                String body = response.body().string();
                Log.d(TAG,"Selected_Event_Comments_Replies send_post_request: \n"+ body);
                FragmentManager fm = getSupportFragmentManager();

//if you added fragment via layout xml
                Selected_Event_Comment_Replies_Fragment fragment =
                        (Selected_Event_Comment_Replies_Fragment)fm.findFragmentById(R.id.fragment_comment_replies);

                Handler mainHandler = new Handler(getMainLooper());
//                Context context = getApplicationContext();
                Runnable myRunnable = () ->
                {
                    Toast.makeText(getApplicationContext(),
                            "Comment has been sent successfully", Toast.LENGTH_LONG).show();
                    onBackPressed();
                    fragment.refresh_replies_list();
                    user_comment_et.getText().clear();

                };
                mainHandler.post(myRunnable);
                /// Check if the string contains "ok"
//                if (body.length()>1)
//                {
//                    String ok_return = body.substring(0, 2);
//                    body = body.substring(2);
//                    Log.d(TAG, "ok folan : \n" + ok_return);
//
//                    Log.d(TAG,"SEND_USER_COMMENT send_post_request: \n"+ body);
//                    if (ok_return.equals("ok"))
//                    {
////                            comments = new Gson().fromJson(body, new TypeToken<List<Selected_Event_Comments_Object>>()
////                            {
////                            }.getType());
////                            Log.d(TAG, "SEND_USER_COMMENT onResponse: \n" + comments);
////                            received_comments_list(comments);
//                    }
//                    else if (body.equals("fail"))
//                        Toast.makeText(getApplicationContext(),
//                                "Failed to Send your Comment", Toast.LENGTH_LONG).show();
//
//
//
//                }
//                else
//                    Toast.makeText(getApplicationContext(),"Connection was Unsuccessful",Toast.LENGTH_LONG).show();



            }
        };

        HTTP_Provider.post_json(getString(R.string.events_comments_followers_handler),request.get_request(),callback);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}