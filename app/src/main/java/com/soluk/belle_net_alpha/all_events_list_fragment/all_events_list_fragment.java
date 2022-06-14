package com.soluk.belle_net_alpha.all_events_list_fragment;

import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_DISPLAY_TYPE;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_TYPE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.soluk.belle_net_alpha.Main_Activity;
import com.soluk.belle_net_alpha.model.Events_DB_VM;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.event_data_maker.file_maker;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A fragment representing a list of Items.
 */
public class all_events_list_fragment extends Fragment
{

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String FILE_NAME = "geo_json_bellenet";

    private int mColumnCount = 1;

    private RecyclerView recycler_view;

    public static final int DISCOVER_MODE = 0;
    public static final int JOINED_MODE = 1;
    public static final int OWN_MODE = 2;
    public static final int ALL_MODE = 3;
    private int mode = 0;

    private Button  discover_btn;
    private Button  joined_btn;
    private Button  own_btn;

    private Button all_events_btn;
    private Button ensemble_events_btn;
    private Button challenge_events_btn;
    private Button experience_events_btn;

    private final int ENSEMBLE = 0;
    private final int CHALLENGE = 1;
    private final int EXPERIENCE = 2;

    private int event_type_current = 3;

    private List<Feature> feature_list_all;
    private List<Feature> feature_list_ensemble;
    private List<Feature> feature_list_challenge;
    private List<Feature> feature_list_experience;
    private SwipeRefreshLayout swipe_refresh_layout;



    public all_events_list_fragment()
    {
    }


    public static all_events_list_fragment newInstance(int columnCount)
    {
        all_events_list_fragment fragment = new all_events_list_fragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_all_events_list, container, false);


        match_items_views(view);
        feature_list_segmentation();
        setup_mode_buttons();
        setup_event_type_buttons();

        /// Swipe to refresh the events
        swipe_refresh_layout.setOnRefreshListener(() ->
        ((Main_Activity) requireActivity()).refresh_db());


        return view;
    }

    void match_items_views(View view)
    {
        Context context = view.getContext();
        recycler_view = view.findViewById(R.id.event_list_recycler);//(RecyclerView) view;
        recycler_view.setLayoutManager(new LinearLayoutManager(context));


        discover_btn = view.findViewById(R.id.discover_btn);
        joined_btn = view.findViewById(R.id.joined_btn);
        own_btn = view.findViewById(R.id.own_btn);

        all_events_btn = view.findViewById(R.id.all_events_btn);
        ensemble_events_btn = view.findViewById(R.id.ensemble_events_btn);
        challenge_events_btn = view.findViewById(R.id.challenge_events_btn);
        experience_events_btn = view.findViewById(R.id.experience_events_btn);

        swipe_refresh_layout = view.findViewById(R.id.refresh_events_srl);
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    private void setup_event_type_buttons()
    {
        all_events_btn.setOnClickListener(v ->
        {
//            default_mode_buttons_setting();
            reset_event_type_buttons();
            all_events_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                    , R.color.palette_teal_light, requireContext().getTheme()));
            all_events_btn.setTextColor(ResourcesCompat.getColor(getResources()
                    , R.color.palette_gray_light, requireContext().getTheme()));
            all_events_btn.setCompoundDrawableTintList(ResourcesCompat.getColorStateList(getResources()
                    ,R.color.palette_gray_light, requireContext().getTheme()));
            event_type_current = 3;
            find_event_type();
        });

        ensemble_events_btn.setOnClickListener(v ->
        {
//            default_mode_buttons_setting();
            reset_event_type_buttons();
            ensemble_events_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                    , R.color.palette_teal_light, requireContext().getTheme()));
            ensemble_events_btn.setTextColor(ResourcesCompat.getColor(getResources()
                    , R.color.gray_100, requireContext().getTheme()));
            ensemble_events_btn.setCompoundDrawableTintList(ResourcesCompat.getColorStateList(getResources()
                    ,R.color.palette_gray_light, requireContext().getTheme()));
            event_type_current = 0;
            find_event_type();
        });

        challenge_events_btn.setOnClickListener(v ->
        {
//            default_mode_buttons_setting();
            reset_event_type_buttons();
            challenge_events_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                    , R.color.palette_teal_light, requireContext().getTheme()));
            challenge_events_btn.setTextColor(ResourcesCompat.getColor(getResources()
                    , R.color.gray_100, requireContext().getTheme()));
            challenge_events_btn.setCompoundDrawableTintList(ResourcesCompat.getColorStateList(getResources()
                    ,R.color.palette_gray_light, requireContext().getTheme()));
            event_type_current = 1;
            find_event_type();
        });

        experience_events_btn.setOnClickListener(v ->
        {
//            default_mode_buttons_setting();
            reset_event_type_buttons();
            experience_events_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                    , R.color.palette_teal_light, requireContext().getTheme()));
            experience_events_btn.setTextColor(ResourcesCompat.getColor(getResources()
                    , R.color.gray_100, requireContext().getTheme()));
            experience_events_btn.setCompoundDrawableTintList(ResourcesCompat.getColorStateList(getResources()
                    ,R.color.palette_gray_light, requireContext().getTheme()));
            event_type_current = 2;
            find_event_type();
        });

    }

    private void default_mode_buttons_setting()
    {
        reset_mode_buttons();
        discover_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                , R.color.palette_teal_light, requireContext().getTheme()));
        discover_btn.setTextColor(ResourcesCompat.getColor(getResources()
                , R.color.gray_100, requireContext().getTheme()));
        mode = DISCOVER_MODE;
    }

    private void default_event_type_buttons_setting()
    {
        reset_event_type_buttons();
        all_events_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                , R.color.palette_teal_light, requireContext().getTheme()));
        all_events_btn.setTextColor(ResourcesCompat.getColor(getResources()
                , R.color.gray_100, requireContext().getTheme()));
        mode = DISCOVER_MODE;
    }

    private void setup_mode_buttons()
    {
        discover_btn.setOnClickListener(v->
        {
            reset_mode_buttons();
            discover_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                    , R.color.palette_teal_light, requireContext().getTheme()));
            discover_btn.setTextColor(ResourcesCompat.getColor(getResources()
                    , R.color.gray_100, requireContext().getTheme()));
            mode = DISCOVER_MODE;
            find_event_type();

        });

        joined_btn.setOnClickListener(v->
        {
            reset_mode_buttons();
            joined_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                    , R.color.palette_teal_light, getContext().getTheme()));
            joined_btn.setTextColor(ResourcesCompat.getColor(getResources()
                    , R.color.gray_100, getContext().getTheme()));
            mode = JOINED_MODE;
            find_event_type();

        });

        own_btn.setOnClickListener(v->
        {
            reset_mode_buttons();
            own_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                    , R.color.palette_teal_light, requireContext().getTheme()));
            own_btn.setTextColor(ResourcesCompat.getColor(getResources()
                    , R.color.gray_100, requireContext().getTheme()));
            mode = OWN_MODE;
            find_event_type();

        });
    }

    private void feature_list_segmentation()
    {
        String dir = requireActivity().getFilesDir().toString();
        file_maker geo_json_holder = new file_maker(dir,FILE_NAME);
        FeatureCollection feature_collection = geo_json_holder.read_features();
        feature_list_all = feature_collection.features();
        feature_list_experience = new ArrayList<>();
        feature_list_challenge = new ArrayList<>();
        feature_list_ensemble = new ArrayList<>();

        assert feature_list_all != null;
        for(Feature feature: feature_list_all)
        {
            int event_type = Integer.parseInt(feature.getStringProperty(EVENT_TYPE));
            switch (event_type)
            {
                case ENSEMBLE:
                    feature_list_ensemble.add(feature); break;
                case CHALLENGE:
                    feature_list_challenge.add(feature); break;
                case EXPERIENCE:
                    feature_list_experience.add(feature); break;
            }
        }
        update_list(feature_list_all);
    }

    public void refresh_list()
    {
        swipe_refresh_layout.setRefreshing(false);
        feature_list_segmentation();
        find_event_type();
    }
    private void find_event_type()
    {
        switch (event_type_current)
        {
            case ENSEMBLE:
                update_list(feature_list_ensemble); break;
            case CHALLENGE:
                update_list(feature_list_challenge); break;
            case EXPERIENCE:
                update_list(feature_list_experience); break;
            default:
                update_list(feature_list_all); break;
        }
    }

    private void update_list(List<Feature> feature_list)
    {
        List<Feature> feature_list_modified = new ArrayList<>();

        assert feature_list_all != null;
        for (Feature feature: feature_list)
        {
            /// Check if the user joined the event
            //Log.d("Main_Activity", "feature_list.get(i): "+feature_list.get(i));
            boolean is_user_joined_event = feature.
                    getStringProperty(Events_DB_VM.IS_USER_JOINED).equals("true");
            feature.addNumberProperty(EVENT_DISPLAY_TYPE,DISCOVER_MODE);

            boolean is_user_owned_event = false;
            try
            {
                String USER_ID = User_Credentials.get_item(Events_DB_VM.USER_ID);
                String event_owner_user_id = feature.getStringProperty(Events_DB_VM.USER_ID);
                is_user_owned_event = event_owner_user_id.equals(USER_ID);

            }
            catch (JSONException e){e.printStackTrace();}

            switch (mode)
            {
                case DISCOVER_MODE:
                    if(!is_user_joined_event&&!is_user_owned_event)
                        feature_list_modified.add(feature); break;
                case JOINED_MODE:
                    if(is_user_joined_event&&!is_user_owned_event)
                        feature_list_modified.add(feature); break;
                case OWN_MODE:
                    if(is_user_owned_event)
                        feature_list_modified.add(feature); break;

            }
        }
        recycler_view.setAdapter(new all_events_list_recycler_view_adapter(feature_list_modified,mode));
    }

    private void reset_mode_buttons()
    {
        discover_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
            , R.color.gray_200, requireContext().getTheme()));
        joined_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                , R.color.gray_200, requireContext().getTheme()));
        own_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                , R.color.gray_200, requireContext().getTheme()));

        discover_btn.setTextColor(ResourcesCompat.getColor(getResources()
                , R.color.gray_500, requireContext().getTheme()));
        joined_btn.setTextColor(ResourcesCompat.getColor(getResources()
                , R.color.gray_500, requireContext().getTheme()));
        own_btn.setTextColor(ResourcesCompat.getColor(getResources()
                , R.color.gray_500, requireContext().getTheme()));

    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    private void reset_event_type_buttons()
    {
        all_events_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                , R.color.palette_teal_dark, requireContext().getTheme()));
        ensemble_events_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                , R.color.palette_teal_dark, requireContext().getTheme()));
        challenge_events_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                , R.color.palette_teal_dark, requireContext().getTheme()));
        experience_events_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
            , R.color.palette_teal_dark, requireContext().getTheme()));


        all_events_btn.setTextColor(ResourcesCompat.getColor(getResources()
                , R.color.palette_teal_light, requireContext().getTheme()));
        ensemble_events_btn.setTextColor(ResourcesCompat.getColor(getResources()
                , R.color.palette_teal_light, requireContext().getTheme()));
        challenge_events_btn.setTextColor(ResourcesCompat.getColor(getResources()
                , R.color.palette_teal_light, requireContext().getTheme()));
        experience_events_btn.setTextColor(ResourcesCompat.getColor(getResources()
                , R.color.palette_teal_light, requireContext().getTheme()));

        all_events_btn.setCompoundDrawableTintList(
                ResourcesCompat.getColorStateList(
                        getResources(),R.color.teal_200, requireContext().getTheme()));
        ensemble_events_btn.setCompoundDrawableTintList(
                ResourcesCompat.getColorStateList
                        (getResources(),R.color.teal_200, requireContext().getTheme()));
        challenge_events_btn.setCompoundDrawableTintList(
                ResourcesCompat.getColorStateList(
                        getResources(),R.color.teal_200, requireContext().getTheme()));
        experience_events_btn.setCompoundDrawableTintList(
                ResourcesCompat.getColorStateList(
                        getResources(),R.color.teal_200, requireContext().getTheme()));

    }
}