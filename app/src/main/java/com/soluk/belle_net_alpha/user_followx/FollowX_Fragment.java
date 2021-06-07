package com.soluk.belle_net_alpha.user_followx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.main_activity;
import com.soluk.belle_net_alpha.user_followx.dummy.DummyContent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class FollowX_Fragment extends Fragment
{

    String TAG = FollowX_Fragment.class.getSimpleName();
    // TODO: Customize parameter argument names
    private static final String ARG_FOLLOW_X = "followx_body";
    private static final String ARG_FOLLOW_X_MODE = "followx_mode";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private String followX;
    static int followX_mode;

    private static final int FOLLOWINGS = 0;
    private static final int FOLLOWERS = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FollowX_Fragment()
    {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FollowX_Fragment newInstance(String body, int x)
    {
        FollowX_Fragment fragment = new FollowX_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_FOLLOW_X, body);
        args.putInt(ARG_FOLLOW_X_MODE, x);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            followX = getArguments().getString(ARG_FOLLOW_X);
            followX_mode = getArguments().getInt(ARG_FOLLOW_X_MODE);
            Log.d(TAG,"Send onResponse: "+ followX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_followx__list, container, false);


        JSONObject followx_json;
        List<FollowX_Object> users = null;
        try
        {
            followx_json = new JSONObject(followX);
            if(followX_mode>0)
                users = new Gson().fromJson(followx_json.get("followers").toString(),
                            new TypeToken<List<FollowX_Object>>() {}.getType());
            else
                users = new Gson().fromJson(followx_json.get("followings").toString(),
                            new TypeToken<List<FollowX_Object>>() {}.getType());

            //((User_FollowX_List_Activity)getActivity()).update_followx_type(followX_mode);

        } catch (JSONException e){e.printStackTrace();}
        //List<FollowX_Object> users = new Gson().fromJson(followx_json.get(), new TypeToken<List<FollowX_Object>>() {}.getType());

        // Set the adapter

        Context context = view.getContext();

        RecyclerView recyclerView = view.findViewById(R.id.user_followx_list_rv);
        if (mColumnCount <= 1)
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        else
        {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        recyclerView.setAdapter(new My_Followx_RecyclerViewAdapter(users));

        return view;
    }
}