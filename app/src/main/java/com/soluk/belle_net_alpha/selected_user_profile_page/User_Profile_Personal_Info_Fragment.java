package com.soluk.belle_net_alpha.selected_user_profile_page;

import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.mapbox.geojson.Feature;
import com.soluk.belle_net_alpha.HTTP_Provider;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;
import com.soluk.belle_net_alpha.utils.Date_Time_Provider;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.os.Looper.getMainLooper;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.OWNER_USER_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_EMAIL;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_FAMILY;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_FOLLOWERS;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_FOLLOWINGS;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_ID_FOLLOWING;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_JOIN_DATE;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_NAME;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_PASSWORD;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_PIC;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_REQUEST;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.profile_pic_url;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link User_Profile_Personal_Info_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class User_Profile_Personal_Info_Fragment extends Fragment
{

    String TAG = User_Profile_Personal_Info_Fragment.class.getSimpleName();
    private static final String ARG_FEATURE = "feature";
    private static final String REQUEST_DB_USER_ID_SUB_URL = "belle_net_users_info/return_user_id.php";
    private static final String REQUEST_DB_FOLLOW_X_USER_SUB_URL = "belle_net_users_info/following_selected_user.php";
    private static final int FOLLOW_COMMAND = 0;
    private static final int UNFOLLOW_COMMAND = 1;
    private static final String FOLLOW_REQUEST = "follow_request";
    private static final String UNFOLLOW_REQUEST = "unfollow_request";

    private String user_feature_string;
    private boolean is_following;

    private Feature user_feature;
    private TextView user_followers_num_tv;
    private TextView user_followings_num_tv;
    private TextView user_join_date_num_tv;
    private MaterialButton follow_btn;

    public User_Profile_Personal_Info_Fragment()
    {
        // Required empty public constructor
    }


    public static User_Profile_Personal_Info_Fragment newInstance(String feature)
    {
        User_Profile_Personal_Info_Fragment fragment = new User_Profile_Personal_Info_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_FEATURE, feature);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            user_feature_string = getArguments().getString(ARG_FEATURE);
            user_feature = Feature.fromJson(user_feature_string);
            //Log.d(TAG,"User feature: "+ feature.getStringProperty("name"));
        }


        /*
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() ->
        {

            handler.post(() ->
            {

            });
        });

         */

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        /// Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile_personal_info, container, false);


        /// GUI elements
        TextView user_name_family_tv = view.findViewById(R.id.user_name_family_tv);
        CircleImageView user_profile_image_civ = view.findViewById(R.id.user_profile_image_civ);
        user_followings_num_tv = view.findViewById(R.id.user_followings_num_tv);
        user_followers_num_tv = view.findViewById(R.id.user_followers_num_tv);
        user_join_date_num_tv = view.findViewById(R.id.user_join_date_num_tv);
        follow_btn = view.findViewById(R.id.follow_btn);


        /// Callback on the arriving user's info
        Callback callback = new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                Log.d(TAG,"onFailure: "+ e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                String body = response.body().string();
                Log.d(TAG,"onResponse: "+ body);

                Handler mainHandler = new Handler(getMainLooper());
                Runnable myRunnable = () ->
                {
                    try
                    {
                        JSONObject user_params = new JSONObject(body);
                        user_followings_num_tv.setText(user_params.get(USER_FOLLOWINGS).toString());
                        user_followers_num_tv.setText(user_params.get(USER_FOLLOWERS).toString());
                        user_join_date_num_tv.setText(Date_Time_Provider
                                .date_to_MDY(user_params.get(USER_JOIN_DATE).toString()));

                        if(!user_params.get("is_user_following").toString().equals("1"))
                        {
                            follow_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                                    , R.color.palette_teal_light, getActivity().getTheme()));
                            follow_btn.setTextColor(ResourcesCompat.getColor(getResources()
                                    , R.color.gray_100, getActivity().getTheme()));
                            follow_btn.setText("Follow");
                            is_following = false;
                        }
                        else
                            is_following = true;

                    } catch (JSONException e) {e.printStackTrace();}
                };
                mainHandler.post(myRunnable);
            }
        };

        JSONObject user_params = new JSONObject();
        try
        {
            user_params.put(USER_ID,user_feature.getStringProperty(USER_ID)); /// Returns the requested user id
            user_params.put(OWNER_USER_ID,User_Credentials.get_item(USER_ID)); /// Returns the current user id
            user_params.put(USER_EMAIL, User_Credentials.get_item(USER_EMAIL));
            user_params.put(USER_PASSWORD,User_Credentials.get_item(USER_PASSWORD));
            HTTP_Provider.post_json(REQUEST_DB_USER_ID_SUB_URL,user_params,callback);

        } catch (JSONException e) {e.printStackTrace();}


        String user_name_family = user_feature.getStringProperty(USER_NAME)+" "+
                user_feature.getStringProperty(USER_FAMILY);

        /// Initiating GUI elements
        user_name_family_tv.setText(user_name_family);
        String path = profile_pic_url + user_feature.getStringProperty(USER_PIC)+ ".jpg";
        Picasso.get().invalidate(path);
        Picasso.get().load(path).networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(user_profile_image_civ);

        follow_btn.setOnClickListener(v ->
        {
            try
            {

                if (is_following)
                {
                    follow_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                            , R.color.palette_teal_light, getActivity().getTheme()));
                    follow_btn.setTextColor(ResourcesCompat.getColor(getResources()
                            , R.color.gray_100, getActivity().getTheme()));
                    follow_btn.setStrokeWidth(0);
                    follow_btn.setText("Follow");
                    is_following = false;
                    send_follow_command(UNFOLLOW_COMMAND);
                }
                else
                {
                    follow_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                            , R.color.gray_200, getActivity().getTheme()));
                    follow_btn.setTextColor(ResourcesCompat.getColor(getResources()
                            , R.color.gray_500, getActivity().getTheme()));
                    follow_btn.setStrokeWidth(1);
                    follow_btn.setText("Following");
                    is_following = true;
                    send_follow_command(FOLLOW_COMMAND);

                }
            }
            catch (Exception e){Log.d(TAG,"try failed: "+ e);}
        });
        return view;
    }

    private void send_follow_command(int command) throws JSONException
    {
        Log.d(TAG,"send_follow_command");
        JSONObject params = new JSONObject();
        params.put(USER_EMAIL, User_Credentials.get_item(USER_EMAIL));
        params.put(USER_PASSWORD, User_Credentials.get_item(USER_PASSWORD));
        params.put(USER_ID, user_feature.getStringProperty(USER_ID)); /// The followed one
        params.put(USER_ID_FOLLOWING, User_Credentials.get_item(USER_ID));  /// The follower
        switch (command)
        {
            case FOLLOW_COMMAND: params.put(USER_REQUEST,FOLLOW_REQUEST);
                                    break;
            case UNFOLLOW_COMMAND: params.put(USER_REQUEST,UNFOLLOW_REQUEST) ;
                                    break;
        }

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
                Log.d(TAG,"onResponse: "+ body);
                if(body.trim().equals("ok"))
                {
                    Log.d(TAG,"OK");

                    Handler mainHandler = new Handler(getMainLooper());
                    Runnable myRunnable;
                    if (command ==FOLLOW_COMMAND)
                        myRunnable = () ->
                        {
                            int follow_count = Integer.parseInt(user_followers_num_tv.
                                    getText().toString()) + 1;
                            user_followers_num_tv.setText(String.valueOf(follow_count));
                        };
                    else

                        myRunnable = () ->
                        {
                            int follow_count = Integer.parseInt(user_followers_num_tv.
                                    getText().toString()) - 1;
                            user_followers_num_tv.setText(String.valueOf(follow_count));
                        };
                    mainHandler.post(myRunnable);
                }
            }
        };

        HTTP_Provider.post_json(REQUEST_DB_FOLLOW_X_USER_SUB_URL,params,callback);

    }

}