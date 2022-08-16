package com.soluk.belle_net_alpha.main_fragments.live_events;

import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_CATEGORY;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_TYPE;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.event_data_maker.file_maker;
import com.soluk.belle_net_alpha.model.Events_DB_VM;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class Offers_List_Fragment extends Fragment
{

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String FILE_NAME = "geo_json_bellenet";

    private final int RECENT = 0;
    private final int HOT = 1;
    private final int TOP = 2;
    private final int JOINED = 3;
    private int mode = 0;

    private final int CHALLENGE = 0;
    private final int DISCOUNT = 1;
    private final int EXPLORE = 2;
    private final int ALL_MODE = 3;

    private Button recent_btn;
    private Button own_btn;
    private Button  hot_btn;
    private Button  top_btn;

    private Button all_btn;
    private Button challenge_btn;
    private Button discount_btn;
    private Button explore_btn;

    private List<Feature> feature_list_all;
    private List<Feature> feature_list_all_received;
    private List<Feature> feature_list_explore;
    private List<Feature> feature_list_discount;
    private List<Feature> feature_list_challenge;

    private RecyclerView recycler_view;
    private SwipeRefreshLayout swipe_refresh_layout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public Offers_List_Fragment()
    {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static Offers_List_Fragment newInstance(int columnCount)
    {
        //        Feed_List_Fragment fragment = new Feed_List_Fragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
//        fragment.setArguments(args);
        return new Offers_List_Fragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null)
//        {
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_feed_list, container, false);

        find_items_views(view);
        feature_list_segmentation();

        return view;
    }

    void find_items_views(View view)
    {
        Context context = view.getContext();
        recycler_view = view.findViewById(R.id.feed_list_recycler);
        recycler_view.setLayoutManager(new GridLayoutManager(context, 2));


        recent_btn = view.findViewById(R.id.recent_btn);
        hot_btn = view.findViewById(R.id.hot_btn);
        top_btn = view.findViewById(R.id.top_btn);
        own_btn = view.findViewById(R.id.own_btn);

        all_btn = view.findViewById(R.id.all_btn);
        challenge_btn = view.findViewById(R.id.challenge_btn);
        discount_btn = view.findViewById(R.id.discount_btn);
        explore_btn = view.findViewById(R.id.explore_btn);

        swipe_refresh_layout = view.findViewById(R.id.refresh_events_srl);
    }


    private void feature_list_segmentation()
    {
        String dir = requireActivity().getFilesDir().toString();
        file_maker geo_json_holder = new file_maker(dir,FILE_NAME);
        FeatureCollection feature_collection = geo_json_holder.read_features();
        feature_list_all_received = feature_collection.features();
        feature_list_all = new ArrayList<>();
        feature_list_challenge = new ArrayList<>();
        feature_list_discount = new ArrayList<>();
        feature_list_explore = new ArrayList<>();

        assert feature_list_all_received != null;
        for(Feature feature: feature_list_all_received)
        {
            /// Checks if the event is user defined:
            int event_category = Integer.parseInt(feature.getStringProperty(EVENT_CATEGORY));
            if (event_category != 1)   continue;

            feature_list_all.add(feature);
            /// Dividing Event types
            int event_type = Integer.parseInt(feature.getStringProperty(EVENT_TYPE));
            switch (event_type)
            {
                case CHALLENGE:
                    feature_list_challenge.add(feature); break;
                case DISCOUNT:
                    feature_list_discount.add(feature); break;
                case EXPLORE:
                    feature_list_explore.add(feature); break;
            }
        }
        update_list(feature_list_all);
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
//            feature.addNumberProperty(EVENT_DISPLAY_TYPE, DISCOVER_MODE);

            boolean is_user_owned_event = false;
            try
            {
                String USER_ID = User_Credentials.get_item(Events_DB_VM.USER_ID);
                String event_owner_user_id = feature.getStringProperty(Events_DB_VM.USER_ID);
                is_user_owned_event = event_owner_user_id.equals(USER_ID);

            }
            catch (JSONException e){e.printStackTrace();}

//            switch (mode)
//            {
//                case RECENT:
//                    if(!is_user_joined_event&&!is_user_owned_event)
//                        feature_list_modified.add(feature); break;
//                case HOT:
//                    if(is_user_joined_event&&!is_user_owned_event)
//                        feature_list_modified.add(feature); break;
//                case TOP:
//                    if(is_user_owned_event)
//                        feature_list_modified.add(feature); break;
//            }
        }
        recycler_view.setAdapter(new Offers_List_Recycler_View_Adapter(feature_list));
    }
}