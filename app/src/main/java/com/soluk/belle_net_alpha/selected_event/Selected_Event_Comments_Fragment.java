package com.soluk.belle_net_alpha.selected_event;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Feature;
import com.soluk.belle_net_alpha.HTTP_Provider;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;
import com.soluk.belle_net_alpha.user_followx.FollowX_Object;
import com.soluk.belle_net_alpha.user_followx.My_Followx_RecyclerViewAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.os.Looper.getMainLooper;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_EMAIL;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_PASSWORD;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_REQUEST;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Selected_Event_Comments_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Selected_Event_Comments_Fragment extends Fragment
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_feature = "feature";
    private String feature_string;
    private static final String REQUEST_DB_USER_FOLLOWERS_SUB_URL = "belle_net_users_info/return_followings_followers_request.php";
    private final String TAG = Selected_Event_Comments_Fragment.class.getSimpleName();
    private RecyclerView recyclerView;

    public Selected_Event_Comments_Fragment()
    {
        // Required empty public constructor
    }


    public static Selected_Event_Comments_Fragment newInstance(String param1)
    {
        Selected_Event_Comments_Fragment fragment = new Selected_Event_Comments_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_feature, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            feature_string = getArguments().getString(ARG_feature);
            Log.d(TAG,"Getting Extra: "+feature_string);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selected_event_comments, container, false);

        Context context = view.getContext();

        Feature feature = Feature.fromJson(feature_string);
        JSONObject user_follow = new JSONObject();
        try
        {
            user_follow.put(EVENT_ID,feature.getStringProperty(EVENT_ID));
            user_follow.put(USER_REQUEST,"event_comments");
            user_follow.put(USER_ID, User_Credentials.get_item(USER_ID));
            user_follow.put(USER_EMAIL,User_Credentials.get_item(USER_EMAIL));
            user_follow.put(USER_PASSWORD,User_Credentials.get_item(USER_PASSWORD));

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
                //Log.d(TAG,"Selected_Event_Comments_Fragment onResponse: \n"+ body);
                if(!body.equals(""))
                {
                    List<Selected_Event_Comments_Object> comments = null;

                    comments = new Gson().fromJson(body, new TypeToken<List<Selected_Event_Comments_Object>>()
                    {
                    }.getType());
                    Log.d(TAG, "Selected_Event_Comments_Fragment onResponse: \n" + comments);
                    received_comments_list(comments);
                }



            }
        };

        HTTP_Provider.post_json(REQUEST_DB_USER_FOLLOWERS_SUB_URL,user_follow,callback);

        recyclerView = view.findViewById(R.id.selected_event_comment_list_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        return view;
    }

    private void received_comments_list(List<Selected_Event_Comments_Object> body)
    {
        /// Accessing the Main UI Thread
        Handler mainHandler = new Handler(getMainLooper());
        Runnable myRunnable = () ->
        {
            recyclerView.setAdapter(new Selected_Event_Comments_RecyclerViewAdapter(body));
        };
        mainHandler.post(myRunnable);
    }
}