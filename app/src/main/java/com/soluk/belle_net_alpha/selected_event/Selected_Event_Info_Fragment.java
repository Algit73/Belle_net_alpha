package com.soluk.belle_net_alpha.selected_event;

import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mapbox.geojson.Feature;
import com.soluk.belle_net_alpha.HTTP_Provider;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.model.Events_DB_VM;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;
import com.soluk.belle_net_alpha.utils.Date_Time_Provider;
import com.soluk.belle_net_alpha.utils.Image_Provider;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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


public class Selected_Event_Info_Fragment extends Fragment
{


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final String TAG = Selected_Event_Info_Fragment.class.getSimpleName();
    private static final String ARG_feature = "feature";
    private String feature_string;
    private JSONObject feature_json;
    private static final String REQUEST_DB_USER_FOLLOWERS_SUB_URL = "belle_net_users_info/return_followings_followers_request.php";
    private TextView event_explanation_tv;

    public Selected_Event_Info_Fragment()
    {
        // Required empty public constructor
    }


    public static Selected_Event_Info_Fragment newInstance(String feature)
    {
        Selected_Event_Info_Fragment fragment = new Selected_Event_Info_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_feature, feature);
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
        View view = inflater.inflate(R.layout.fragment_selected_event_info, container, false);

        TextView event_start_date_tv = view.findViewById(R.id.event_start_date_tv);
        TextView event_end_date_tv = view.findViewById(R.id.event_end_date_tv);
        TextView event_start_time_tv = view.findViewById(R.id.event_start_time_tv);
        TextView event_end_time_tv = view.findViewById(R.id.event_end_time_tv);
        TextView event_joinees_tv = view.findViewById(R.id.event_joinees_tv);
        TextView user_name_family_tv = view.findViewById(R.id.user_name_family_tv);
        CircleImageView user_profile_image_civ = view.findViewById(R.id.user_profile_image_civ);
        Button join_event_btn = view.findViewById(R.id.join_event_btn);
        event_explanation_tv = view.findViewById(R.id.event_explanation_tv);



        Feature feature = Feature.fromJson(feature_string);

        String start_date_time = feature.getStringProperty(Events_DB_VM.EVENT_DATE) + " " +
                feature.getStringProperty(Events_DB_VM.EVENT_TIME);
        String end_date_time = feature.getStringProperty(Events_DB_VM.EVENT_DATE_END) + " " +
                feature.getStringProperty(Events_DB_VM.EVENT_TIME_END);

//        Log.d(TAG,"Date_time start_date_time: "+ start_date_time);
//        Log.d(TAG,"Date_time end_date_time: "+ end_date_time);

        try
        {
            start_date_time = Date_Time_Provider.localizing(start_date_time);
            String start_date =  start_date_time.substring(0,9);
            String start_time =  start_date_time.substring(10);

            end_date_time = Date_Time_Provider.localizing(end_date_time);
            String end_date =  end_date_time.substring(0,9);
            String end_time =  end_date_time.substring(10);

            event_start_date_tv.setText(Date_Time_Provider.date_to_MDY(start_date));
            event_start_time_tv.setText(Date_Time_Provider.time_reformat(start_time));
            Log.d(TAG,"Date_time end_date_time: "+ end_date_time);
            Log.d(TAG,"Date_time end_date: "+ end_date);
            Log.d(TAG,"Date_time end_time: "+ end_time);
            event_end_date_tv.setText(Date_Time_Provider.date_to_MDY(end_date));
            event_end_time_tv.setText(Date_Time_Provider.time_reformat(end_time));
        }
        catch (Exception e){Log.d(TAG,"Date_time Parse Error: "+ e);}

        String user_name_family = feature.getStringProperty(Events_DB_VM.USER_NAME) + " "+
                feature.getStringProperty(Events_DB_VM.USER_FAMILY);

        event_joinees_tv.setText(feature.getStringProperty(Events_DB_VM.NUM_OF_JOINED));
        user_name_family_tv.setText(user_name_family);
        user_profile_image_civ.setImageBitmap(Image_Provider
                                .get_profile_bmp(feature.getStringProperty(Events_DB_VM.USER_PIC)));

        if(feature.getStringProperty(Events_DB_VM.IS_USER_JOINED).equals("true"))
        {
            join_event_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                    , R.color.gray_200, getActivity().getTheme()));
            join_event_btn.setText("Opt out");
            join_event_btn.setTextColor(ResourcesCompat.getColor(getResources()
                    , R.color.gray_800, getActivity().getTheme()));
        }
        else
        {
            join_event_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                    , R.color.palette_teal_light, getActivity().getTheme()));
            join_event_btn.setText("Join Now");
            join_event_btn.setTextColor(ResourcesCompat.getColor(getResources()
                    , R.color.gray_100, getActivity().getTheme()));

        }



        request_event_description(feature);




        join_event_btn.setOnClickListener(v->
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
                    Log.d(TAG, "onResponse: Text upload ->"+response.body().string());

                }
            };

            try
            {
                JSONObject json = new JSONObject();
                json.put("alpha",1);
                json.put("beta",2);
                HTTP_Provider.upload_text(json,"this is only a test",callback);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }


        });

        return view;
    }



    private void request_event_description(Feature feature)
    {
        JSONObject user_follow = new JSONObject();
        try
        {
            user_follow.put(EVENT_ID,feature.getStringProperty(EVENT_ID));
            user_follow.put(USER_REQUEST,"event_description");
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
                Log.d(TAG,"onResponse event_description: "+ body);
                update_event_description(body);

            }
        };

        HTTP_Provider.post_json(REQUEST_DB_USER_FOLLOWERS_SUB_URL,user_follow,callback);
    }

    private void update_event_description(String description)
    {
        /// Accessing the Main UI Thread
        Handler mainHandler = new Handler(getMainLooper());
        Runnable myRunnable = () ->
        {
            event_explanation_tv.setText(description);
        };
        mainHandler.post(myRunnable);
    }
}