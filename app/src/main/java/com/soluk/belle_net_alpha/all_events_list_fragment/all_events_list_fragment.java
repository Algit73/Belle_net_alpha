package com.soluk.belle_net_alpha.all_events_list_fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 */
public class all_events_list_fragment extends Fragment
{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public all_events_list_fragment()
    {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
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
        RecyclerView recyclerView = v.findViewById(R.id.event_list_recycler);//(RecyclerView) view;
        if (mColumnCount <= 1)
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        else
        {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        recyclerView.setAdapter(new all_events_list_recycler_view_adapter(DummyContent.ITEMS));
        //recyclerView.setHasFixedSize(false);
        //ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        //v.setNestedScrollingEnabled(false);

        return v;
    }
}