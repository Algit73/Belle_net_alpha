package com.soluk.belle_net_alpha.selected_event;

import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mapbox.geojson.Feature;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.model.Events_DB_VM;
import com.soluk.belle_net_alpha.utils.Date_Time_Provider;
import com.soluk.belle_net_alpha.utils.Image_Provider;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;


public class Selected_Event_Info_Fragment extends Fragment
{


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final String TAG = Selected_Event_Info_Fragment.class.getSimpleName();
    private static final String ARG_feature = "feature";
    private String feature_string;
    private JSONObject feature_json;

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
        TextView event_start_time_tv = view.findViewById(R.id.event_start_time_tv);
        TextView event_joinees_tv = view.findViewById(R.id.event_joinees_tv);
        TextView user_name_family_tv = view.findViewById(R.id.user_name_family_tv);
        CircleImageView user_profile_image_civ = view.findViewById(R.id.user_profile_image_civ);
        Button join_event_btn = view.findViewById(R.id.join_event_btn);


        Feature feature = Feature.fromJson(feature_string);
        String user_name_family = feature.getStringProperty(Events_DB_VM.USER_NAME) + " "+
                feature.getStringProperty(Events_DB_VM.USER_FAMILY);
        event_start_date_tv.setText(feature.getStringProperty(Events_DB_VM.EVENT_DATE));
        event_start_time_tv.setText(Date_Time_Provider.time_reformat(feature.getStringProperty(Events_DB_VM.EVENT_TIME)));
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
                    , R.color.blue_900, getActivity().getTheme()));
            join_event_btn.setText("Join Now");
            join_event_btn.setTextColor(ResourcesCompat.getColor(getResources()
                    , R.color.gray_100, getActivity().getTheme()));

        }




        return view;
    }
}