package com.soluk.belle_net_alpha.selected_event;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Feature;
import com.soluk.belle_net_alpha.HTTP_Provider;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;

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
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_EMAIL;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_PASSWORD;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_REQUEST;

/**
 * A fragment representing a list of Items.
 */
public class Selected_Event_Comment_Replies_Fragment extends Fragment
{

    private static final String ARG_COMMENT_ID = "comment_id";
    private static final String REQUEST_DB_USER_FOLLOWERS_SUB_URL = "belle_net_users_info/return_followings_followers_request.php";
    private final String TAG = Selected_Event_Comment_Replies_Fragment.class.getSimpleName();
    private String comment_Id;
    private RecyclerView recyclerView;


    public Selected_Event_Comment_Replies_Fragment()
    {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static Selected_Event_Comment_Replies_Fragment newInstance(String comment_id)
    {
        Selected_Event_Comment_Replies_Fragment fragment = new Selected_Event_Comment_Replies_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_COMMENT_ID, comment_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            comment_Id = getArguments().getString(ARG_COMMENT_ID);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_selected_event_comment_replies, container, false);

        Context context = view.getContext();

        //Feature feature = Feature.fromJson(comment_Id);
        JSONObject user_follow = new JSONObject();
        try
        {
            user_follow.put(COMMENT_ID,comment_Id);
            user_follow.put(USER_REQUEST,"event_comment_replies");
            user_follow.put(USER_ID, User_Credentials.get_item(USER_ID));
            user_follow.put(USER_EMAIL,User_Credentials.get_item(USER_EMAIL));
            user_follow.put(USER_PASSWORD,User_Credentials.get_item(USER_PASSWORD));
            //Log.d(TAG,"user creds: "+ user_follow);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Callback callback = new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                String body = response.body().string();
//                Log.d(TAG,"Selected_Event_Comments_Fragment onResponse: \n"+ body);
                if(!body.equals(""))
                {
                    List<Selected_Event_Comment_Replies_Object> comments = null;

                    comments = new Gson().fromJson(body, new TypeToken<List<Selected_Event_Comment_Replies_Object>>()
                    {
                    }.getType());
//                    Log.d(TAG, "Selected_Event_Comments_Fragment onResponse: \n" + comments);
                    received_comments_list(comments);
                }







            }
        };

        HTTP_Provider.post_json(REQUEST_DB_USER_FOLLOWERS_SUB_URL,user_follow,callback);

        recyclerView = view.findViewById(R.id.selected_event_comment_replies_list_rv);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return view;
    }

    private void received_comments_list(List<Selected_Event_Comment_Replies_Object> body)
    {
        /// Accessing the Main UI Thread
        Handler mainHandler = new Handler(getMainLooper());
        Runnable myRunnable = () ->
        {
            recyclerView.setAdapter(new Selected_Event_Comment_Replies_RecyclerViewAdapter(body));
        };
        mainHandler.post(myRunnable);
    }



}