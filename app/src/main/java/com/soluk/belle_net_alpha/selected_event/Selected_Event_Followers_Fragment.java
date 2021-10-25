package com.soluk.belle_net_alpha.selected_event;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Feature;
import com.soluk.belle_net_alpha.HTTP_Provider;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.model.Events_DB_VM;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;
import com.soluk.belle_net_alpha.user_followx.FollowX_Object;
import com.soluk.belle_net_alpha.user_followx.User_FollowX_List_Activity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
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
 * Use the {@link Selected_Event_Followers_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Selected_Event_Followers_Fragment extends Fragment
{

    private final String TAG = Selected_Event_Followers_Fragment.class.getSimpleName();
    private static final String ARG_feature = "feature";
    private String feature_string;
    private static final String REQUEST_DB_USER_FOLLOWERS_SUB_URL = "belle_net_users_info/return_followings_followers_request.php";

    private LinearLayout followers_holder_ll;

    public Selected_Event_Followers_Fragment()
    {
        // Required empty public constructor
    }


    public static Selected_Event_Followers_Fragment newInstance(String param1)
    {
        Selected_Event_Followers_Fragment fragment = new Selected_Event_Followers_Fragment();
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
        View view = inflater.inflate(R.layout.fragment_selected_event_followers, container, false);

        Feature feature = Feature.fromJson(feature_string);
        JSONObject user_follow = new JSONObject();
        try
        {
            user_follow.put(EVENT_ID,feature.getStringProperty(EVENT_ID));
            user_follow.put(USER_REQUEST,"event_followers");
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
                Log.d(TAG,"onResponse: "+ body);
                received_followers_list(body);

            }
        };

        HTTP_Provider.post_json(REQUEST_DB_USER_FOLLOWERS_SUB_URL,user_follow,callback);

        followers_holder_ll = view.findViewById(R.id.followers_holder_ll);


        return view;
    }

    void received_followers_list(String response)
    {
        try
        {
            JSONObject body_json = new JSONObject(response);
            List<Selected_Event_Followers_Object> event_followers = new Gson().fromJson(body_json.get("event_followers").toString(),
                    new TypeToken<List<Selected_Event_Followers_Object>>() {}.getType());
            for(int i=0;i<event_followers.size();i++)
            {
                String path = Events_DB_VM.profile_pic_url + event_followers.get(i).user_picture + ".jpg";
                receiving_user_picture(path);

            }
        }
        catch (Exception e){Log.d(TAG,"catch error: "+ e);}

    }

    void receiving_user_picture(String path)
    {
        /// Accessing the Main UI Thread
        Handler mainHandler = new Handler(getMainLooper());
        Runnable myRunnable = () ->
        {
            Target target = new Target()
            {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                {
                    //holder.user_profile_image_civ.setImageBitmap(bitmap);
                    Log.d(TAG,"onBitmapLoaded: ");

                    inflate_CircleImageView(bitmap);

                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable)
                {

                    inflate_CircleImageView(null);

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable)
                {

                }
            };

            Picasso.get().load(path).into(target);
        };
        mainHandler.post(myRunnable);
    }

    void inflate_CircleImageView(Bitmap bitmap)
    {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150,150);
        layoutParams.setMargins(15, 0, 15, 0);

        CircleImageView follower_profile_image = new CircleImageView(getContext());
        follower_profile_image.setBorderColor(getResources().
                getColor(R.color.teal_palette_light,getActivity().getTheme()));
        follower_profile_image.setBorderWidth(5);
        follower_profile_image.setLayoutParams(new ViewGroup.LayoutParams(50, 50));
        if(bitmap!=null)
            follower_profile_image.setImageBitmap(bitmap);
        else
            follower_profile_image.setImageResource(R.drawable.default_profile_pic);
        followers_holder_ll.addView(follower_profile_image,layoutParams);
    }

}

