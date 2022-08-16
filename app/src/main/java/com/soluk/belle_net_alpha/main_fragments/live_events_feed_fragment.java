package com.soluk.belle_net_alpha.main_fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.main_fragments.live_events.Live_Events_List_Fragment;
import com.soluk.belle_net_alpha.main_fragments.live_events.Offers_List_Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link live_events_feed_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class live_events_feed_fragment extends Fragment
{

    private static final String TAG = live_events_feed_fragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private Live_Events_List_Fragment all_events_list;
    private Offers_List_Fragment feed_list;

    private FragmentTransaction fragment_transaction;

    public live_events_feed_fragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment event_list_fragment_parent.
     */
    // TODO: Rename and change types and number of parameters
    public static live_events_feed_fragment newInstance(String param1, String param2)
    {
        live_events_feed_fragment fragment = new live_events_feed_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_events_hub_feed, container, false);
        TabLayout tab_layout = v.findViewById(R.id.event_list_parent_tab);

        all_events_list = new Live_Events_List_Fragment();
        feed_list = new Offers_List_Fragment();

        fragment_transaction = getChildFragmentManager().beginTransaction().setReorderingAllowed(true)
                                    .add(R.id.all_events_list,all_events_list,null)
                                    .add(R.id.feed_list,feed_list,null)
                                    .hide(feed_list);
        fragment_transaction.commit();

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {

                switch (tab.getPosition())
                {

                    case 0:
                        fragment_transaction = getChildFragmentManager().beginTransaction();
                        fragment_transaction.show(all_events_list).hide(feed_list).commit();
                        Log.d(TAG,"all_events_list");

                        break;
                    case 1:
                        fragment_transaction = getChildFragmentManager().beginTransaction();
                        fragment_transaction.show(feed_list).hide(all_events_list).commit();
                        Log.d(TAG,"feed_list");
                        break;
                    case 2:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {
                Log.d(TAG,"onTabUnselected");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {
                Log.d(TAG,"onTabReselected");
            }
        });

        return v;
    }

    public void refresh_fragments()
    {
        /*
        String dir = getActivity().getFilesDir().toString();
        String FILE_NAME = "geo_json_bellenet";
        file_maker geo_json_holder = new file_maker(dir,FILE_NAME);
        FeatureCollection feature_collection = geo_json_holder.read_features();
        List<Feature> feature_list = feature_collection.features();

         */
        all_events_list.refresh_list();




    }
}