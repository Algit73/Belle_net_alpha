package com.soluk.belle_net_alpha.selected_event;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Feature;
import com.soluk.belle_net_alpha.HTTP_Provider;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.utils.Post_Request_Creator;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.os.Looper.getMainLooper;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_COMMENT;
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
    private Feature event_feature;
    private List<Selected_Event_Comments_Object> comments = null;
    private static final int REFRESH_COMMENTS_LIST = 0;
    private static final int SEND_USER_COMMENT = 1;
    private final int REMOVE_USER_COMMENT = 1;


    public Selected_Event_Comments_Fragment()
    {
        // Required empty public constructor
    }

    Selected_Event_Comments_RecyclerViewAdapter.fragment_adapter_interface adapter_interface = command ->
    {
        if (command==REMOVE_USER_COMMENT)
        {
            refresh_comments_list();
            Handler mainHandler = new Handler(getMainLooper());
            Runnable myRunnable = () ->
            {
                Toast.makeText(getContext(),
                        "Comment has been removed successfully", Toast.LENGTH_SHORT).show();
//                refresh_comments_list();
            };
            mainHandler.post(myRunnable);
//            Toast.makeText(getContext(),"Comment has been removed successfully",Toast.LENGTH_SHORT).show();
        }
    };


    // Do whatever you wants to do with this data that is coming from your adapter


    /// Receiving comment from Selected_Event_Add_Comment_Activity

    ActivityResultLauncher<Intent> launch_add_comment_activity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->
            {
                if (result.getResultCode() == Activity.RESULT_OK)
                {
                    Intent data = result.getData();
                    String user_comment = data.getStringExtra(USER_COMMENT);
//                    JSONObject user_follow = new JSONObject();
                    try
                    {
                        Post_Request_Creator request = new Post_Request_Creator();
                        request.add(EVENT_ID,event_feature.getStringProperty(EVENT_ID));
                        request.add(USER_REQUEST,"event_add_comment");
                        request.add(USER_COMMENT,user_comment);
                        send_post_request(request);
                    } catch (JSONException e){e.printStackTrace();}


                }
            });


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

        /// Sending user's request to retrieve the comments
        event_feature = Feature.fromJson(feature_string);
        refresh_comments_list();


//        Callback callback = new Callback()
//        {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e)
//            {}
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
//            {
//                String body = response.body().string();
////                Log.d(TAG,"Selected_Event_Comments_Fragment onResponse: \n"+ body);
//                if(!body.equals(""))
//                {
////                    List<Selected_Event_Comments_Object> comments = null;
//
//                    comments = new Gson().fromJson(body, new TypeToken<List<Selected_Event_Comments_Object>>()
//                    {}.getType());
//                    Log.d(TAG, "Selected_Event_Comments_Fragment onResponse: \n" + comments);
//                    received_comments_list(comments);
//                }
//
//
//
//            }
//        };
//
//        HTTP_Provider.post_json(REQUEST_DB_USER_FOLLOWERS_SUB_URL,request.,callback);

        recyclerView = view.findViewById(R.id.selected_event_comment_list_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        /// Adding comment button
        TextView add_comment_tv = view.findViewById(R.id.add_comment_tv);
        add_comment_tv.setOnClickListener(v ->
        {
            Intent intent = new Intent(getContext(), Selected_Event_Add_Comment_Activity.class);
            intent.putExtra(EVENT_ID, event_feature.getStringProperty(EVENT_ID));
            intent.putExtra(EVENT_ID, event_feature.getStringProperty(EVENT_ID));
            launch_add_comment_activity.launch(intent);
            //getContext().startActivity(intent);
        });


        return view;
    }

//    public void toast()
//    {
//        Toast.makeText(getContext(),"Toasting the roasting: ",Toast.LENGTH_LONG).show();
//    }

    private void refresh_comments_list()
    {
        Post_Request_Creator request = null;
        try
        {
            request = new Post_Request_Creator();
            request.add(EVENT_ID,event_feature.getStringProperty(EVENT_ID));
            request.add(USER_REQUEST,"event_comments");
//            send_post_request(request,REFRESH_COMMENTS_LIST);
        } catch (JSONException e){e.printStackTrace();}

        Callback callback = new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                String body = response.body().string();
                Log.d(TAG,"Selected_Event_Comments_Fragment send_post_request: \n"+ body);
                /// Check if the string contains "ok"
                if (body.length()>1)
                {
                    String ok_return = body.substring(0, 2);
                    body = body.substring(2);
                    Log.d(TAG, "ok folan : \n" + ok_return);

                    if (!body.equals(""))
                    {
                        comments = new Gson().fromJson(body, new TypeToken<List<Selected_Event_Comments_Object>>()
                        {
                        }.getType());
                        Log.d(TAG, "REFRESH_COMMENTS_LIST onResponse: \n" + comments);
                        received_comments_list(comments);
                    }

                }
                else
                    Toast.makeText(getContext(),"Connection was Unsuccessful",Toast.LENGTH_LONG).show();

            }
        };

        HTTP_Provider.post_json(REQUEST_DB_USER_FOLLOWERS_SUB_URL,request.get_request(),callback);
    }

    private void received_comments_list(List<Selected_Event_Comments_Object> body)
    {
        /// Accessing the Main UI Thread
        Handler mainHandler = new Handler(getMainLooper());
        Runnable myRunnable = () ->
        {
            recyclerView.setAdapter(new Selected_Event_Comments_RecyclerViewAdapter(body, adapter_interface));
        };
        mainHandler.post(myRunnable);
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
                Log.d(TAG,"Selected_Event_Comments_Fragment send_post_request: \n"+ body);

                Log.d(TAG,"SEND_USER_COMMENT send_post_request: \n"+ body);
                if (body.equals("ok"))
                {
                    if(getActivity() != null)
                    {
                        Log.d(TAG,"SEND_USER_COMMENT Activity is not null: \n"+ body);
                        Handler mainHandler = new Handler(getMainLooper());
                        Runnable myRunnable = () ->
                        {
                            Toast.makeText(getContext(),
                                    "Comment has been sent successfully", Toast.LENGTH_LONG).show();
                            refresh_comments_list();
                        };
                        mainHandler.post(myRunnable);
                    }
                }
            }
        };

        HTTP_Provider.post_json(REQUEST_DB_USER_FOLLOWERS_SUB_URL,request.get_request(),callback);
    }
}