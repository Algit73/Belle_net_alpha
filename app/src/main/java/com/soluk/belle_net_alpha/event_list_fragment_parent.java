package com.soluk.belle_net_alpha;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.soluk.belle_net_alpha.all_events_list_fragment.all_events_list_fragment;
import com.soluk.belle_net_alpha.feed_list_fragment.feed_list_fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link event_list_fragment_parent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class event_list_fragment_parent extends Fragment
{

    private static final String TAG = event_list_fragment_parent.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private all_events_list_fragment all_events_list;
    private feed_list_fragment feed_list;

    private TabLayout tab_layout;
    private FragmentTransaction fragment_transaction;

    public event_list_fragment_parent()
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
    public static event_list_fragment_parent newInstance(String param1, String param2)
    {
        event_list_fragment_parent fragment = new event_list_fragment_parent();
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
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_event_list_parent, container, false);
        tab_layout = v.findViewById(R.id.event_list_parent_tab);

        all_events_list = new all_events_list_fragment();
        feed_list = new feed_list_fragment();

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
}