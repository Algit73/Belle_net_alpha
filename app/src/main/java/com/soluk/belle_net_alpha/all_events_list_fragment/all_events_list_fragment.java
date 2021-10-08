package com.soluk.belle_net_alpha.all_events_list_fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.soluk.belle_net_alpha.model.Events_DB_VM;
import com.soluk.belle_net_alpha.recycler_view_get_index.CustomTouchListener;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.event_data_maker.file_maker;
import com.soluk.belle_net_alpha.recycler_view_get_index.onItemClickListener;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

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
    private int mode = 0;

    private Button  discover_btn;
    private Button  joined_btn;
    private Button  own_btn;



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
        //View view = inflater.inflate(R.layout.fragment_all_events_list, container, false);
        View view = inflater.inflate(R.layout.fragment_all_events_list, container, false);

        Context context = view.getContext();
        recycler_view = view.findViewById(R.id.event_list_recycler);//(RecyclerView) view;
        if (mColumnCount <= 1)
        {
            recycler_view.setLayoutManager(new LinearLayoutManager(context));
        }
        else
        {
            recycler_view.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        discover_btn = view.findViewById(R.id.discover_btn);
        joined_btn = view.findViewById(R.id.joined_btn);
        own_btn = view.findViewById(R.id.own_btn);

        discover_btn.setOnClickListener(v->
        {
            reset_owning_buttons();
            discover_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                    , R.color.teal_palette_light, getContext().getTheme()));
            discover_btn.setTextColor(ResourcesCompat.getColor(getResources()
                    , R.color.gray_100, getContext().getTheme()));
            mode = DISCOVER_MODE;
            refresh_list();

        });

        joined_btn.setOnClickListener(v->
        {
            reset_owning_buttons();
            joined_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                    , R.color.teal_palette_light, getContext().getTheme()));
            joined_btn.setTextColor(ResourcesCompat.getColor(getResources()
                    , R.color.gray_100, getContext().getTheme()));
            mode = JOINED_MODE;
            refresh_list();

        });

        own_btn.setOnClickListener(v->
        {
            reset_owning_buttons();
            own_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                    , R.color.teal_palette_light, getContext().getTheme()));
            own_btn.setTextColor(ResourcesCompat.getColor(getResources()
                    , R.color.gray_100, getContext().getTheme()));
            mode = OWN_MODE;
            refresh_list();

        });

        return view;
    }

    public void refresh_list()
    {
        String dir = getActivity().getFilesDir().toString();
        file_maker geo_json_holder = new file_maker(dir,FILE_NAME);
        FeatureCollection feature_collection = geo_json_holder.read_features();
        List<Feature> feature_list = feature_collection.features();
        List<Feature> feature_list_modified = new ArrayList<>();

        for(int i=0; i<feature_list.size(); i++)
        {
            /// Check if the user joined the event
            //Log.d("Main_Activity", "feature_list.get(i): "+feature_list.get(i));
            boolean is_user_joined_event = feature_list.get(i).
                        getStringProperty(Events_DB_VM.IS_USER_JOINED).equals("true");

            boolean is_user_owned_event = false;
            try
            {
                String USER_ID = User_Credentials.get_item(Events_DB_VM.USER_ID);
                String event_owner_user_id = feature_list.get(i).getStringProperty(Events_DB_VM.USER_ID);
                is_user_owned_event = event_owner_user_id.equals(USER_ID);

            }
            catch (JSONException e){e.printStackTrace();}

            switch (mode)
            {
                case DISCOVER_MODE:
                    if(!is_user_joined_event&&!is_user_owned_event)
                        feature_list_modified.add(feature_list.get(i)); break;
                case JOINED_MODE:
                    if(is_user_joined_event&&!is_user_owned_event)
                        feature_list_modified.add(feature_list.get(i)); break;
                case OWN_MODE:
                    if(is_user_owned_event)
                        feature_list_modified.add(feature_list.get(i)); break;

                }
        }

        recycler_view.setAdapter(new all_events_list_recycler_view_adapter(feature_list_modified,mode));
    }

    private void reset_owning_buttons()
    {
        discover_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
            , R.color.gray_200, getContext().getTheme()));
        joined_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                , R.color.gray_200, getContext().getTheme()));
        own_btn.setBackgroundColor(ResourcesCompat.getColor(getResources()
                , R.color.gray_200, getContext().getTheme()));

        discover_btn.setTextColor(ResourcesCompat.getColor(getResources()
                , R.color.gray_500, getContext().getTheme()));
        joined_btn.setTextColor(ResourcesCompat.getColor(getResources()
                , R.color.gray_500, getContext().getTheme()));
        own_btn.setTextColor(ResourcesCompat.getColor(getResources()
                , R.color.gray_500, getContext().getTheme()));

    }
}