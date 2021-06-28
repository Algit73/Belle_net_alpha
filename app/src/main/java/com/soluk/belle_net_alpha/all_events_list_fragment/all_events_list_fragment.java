package com.soluk.belle_net_alpha.all_events_list_fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.soluk.belle_net_alpha.recycler_view_get_index.CustomTouchListener;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.event_data_maker.file_maker;
import com.soluk.belle_net_alpha.recycler_view_get_index.onItemClickListener;

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
        View v = inflater.inflate(R.layout.fragment_all_events_list, container, false);

        Context context = v.getContext();
        recycler_view = v.findViewById(R.id.event_list_recycler);//(RecyclerView) view;
        if (mColumnCount <= 1)
        {
            recycler_view.setLayoutManager(new LinearLayoutManager(context));
        }
        else
        {
            recycler_view.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        /*
        recycler_view.addOnItemTouchListener(new CustomTouchListener(getContext(), new onItemClickListener()
        {
            @Override
            public void onClick(View view, int index)
            {
                Toast.makeText(getContext(),"Index: "+index,Toast.LENGTH_SHORT).show();

            }
        }));

         */
        //recycler_view.setAdapter(new all_events_list_recycler_view_adapter(DummyContent.ITEMS));
        //recycler_view.setHasFixedSize(false);
        //ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        //v.setNestedScrollingEnabled(false);

        return v;
    }

    public void refresh_list()
    {
        String dir = getActivity().getFilesDir().toString();
        file_maker geo_json_holder = new file_maker(dir,FILE_NAME);
        FeatureCollection feature_collection = geo_json_holder.read_features();
        List<Feature> feature_list = feature_collection.features();

        recycler_view.setAdapter(new all_events_list_recycler_view_adapter(feature_list));
    }
}