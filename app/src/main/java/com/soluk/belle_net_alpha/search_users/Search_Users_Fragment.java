package com.soluk.belle_net_alpha.search_users;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.soluk.belle_net_alpha.HTTP_Provider;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.search_users.dummy.DummyContent;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;
import com.soluk.belle_net_alpha.user_followx.FollowX_Object;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.os.Looper.getMainLooper;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_EMAIL;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_NAME_FAMILY;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_PASSWORD;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_REQUEST;


/**
 * A fragment representing a list of Items.
 */
public class Search_Users_Fragment extends Fragment
{

    private final String TAG = Search_Users_Fragment.class.getSimpleName();
    private static final String REQUEST_DB_SEARCHED_USERS = "belle_net_users_info/search_user_names.php";
    // TODO: Customize parameter argument names
    private static final String ARG_BODY = "body";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private RecyclerView recyclerView;
    private List<Returned_User_Info_Object> users;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public Search_Users_Fragment()
    {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static Search_Users_Fragment newInstance(String body)
    {
        Search_Users_Fragment fragment = new Search_Users_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_BODY, body);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mColumnCount = getArguments().getInt(ARG_BODY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search_users_list, container, false);



            // Set the adapter

        Context context = view.getContext();

        recyclerView = view.findViewById(R.id.searched_users_list_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        EditText search_input_et = view.findViewById(R.id.search_input_et);
        search_input_et.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String input = s.toString().trim();
                if(!input.isEmpty())
                    send_user_request(input);
                else
                    recyclerView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });







        return view;
    }

    void send_user_request(String input)
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
                Log.d(TAG,"Searched Userd onResponse: "+body);

                JSONArray searched_users_info_json;

                try
                {
                    searched_users_info_json = new JSONArray(body);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Handler handler = new Handler(Looper.getMainLooper());
                    executor.execute(() ->
                    {
                        users = new Gson().fromJson(searched_users_info_json.toString(),
                                new TypeToken<List<Returned_User_Info_Object>>() {}.getType());

                        handler.post(() ->
                        {
                            Log.d(TAG, "searched users: " + users.toString());
                            recyclerView.setAdapter(new Searched_Users_Recycler_View_Adapter(users));
                            recyclerView.setVisibility(View.VISIBLE);
                        });
                    });
                    //users = new Gson().fromJson(searched_users_info_json.toString(),
                      //      new TypeToken<List<Returned_User_Info_Object>>() {}.getType());
                    //Log.d(TAG, "user: "+users.get(1));
                    //recyclerView.setAdapter(new Searched_Users_Recycler_View_Adapter(users));

                    /*
                    Handler mainHandler = new Handler(getMainLooper());
                    Runnable myRunnable = () ->
                    {recyclerView.setAdapter(new Searched_Users_Recycler_View_Adapter(users));};
                    mainHandler.post(myRunnable);

                     */


                    //recyclerView.setAdapter(new Searched_Users_Recycler_View_Adapter(users));

                }
                catch (JSONException e)
                {
                    Log.d(TAG,"Searched Users fault: "+e);
                }



            }
        };

        JSONObject user_follow = new JSONObject();
        try
        {
            user_follow.put(USER_ID, User_Credentials.get_item(USER_ID));
            user_follow.put(USER_EMAIL,User_Credentials.get_item(USER_EMAIL));
            user_follow.put(USER_PASSWORD,User_Credentials.get_item(USER_PASSWORD));
            user_follow.put(USER_NAME_FAMILY,input);
            user_follow.put(USER_REQUEST,"search_request");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        HTTP_Provider.post_json(REQUEST_DB_SEARCHED_USERS,user_follow,callback);
    }
}