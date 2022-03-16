package com.soluk.belle_net_alpha.selected_event;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.soluk.belle_net_alpha.R;



public class Selected_Event_Activity extends AppCompatActivity
{
    private FragmentTransaction fragment_transaction;
    Selected_Event_Map_Fragment selected_event_map_fragment;
    Selected_Event_Info_Fragment selected_event_info_fragment;
    Selected_Event_Followers_Fragment selected_event_followers_fragment;
    Selected_Event_Comments_Fragment selected_event_comments_fragment;
    private NestedScrollView nested_scroll_view_nsv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_event);

        Intent intent = getIntent();
        String extra = intent.getStringExtra("feature");

        selected_event_map_fragment = Selected_Event_Map_Fragment.newInstance(extra);
        selected_event_info_fragment = Selected_Event_Info_Fragment.newInstance(extra);
        selected_event_followers_fragment = Selected_Event_Followers_Fragment.newInstance(extra);
        selected_event_comments_fragment =  Selected_Event_Comments_Fragment.newInstance(extra);
        //nested_scroll_view_nsv.findViewById(R.id.event_info_nsv);

        if (savedInstanceState == null)
        {
            fragment_transaction =  getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_map_section, selected_event_map_fragment, null)
                    .add(R.id.fragment_info_section, selected_event_info_fragment,null)
                    .add(R.id.fragment_followers_section, selected_event_followers_fragment,null)
                    .add(R.id.fragment_reviews_section, selected_event_comments_fragment,null);
            fragment_transaction.commit();
        }

        ImageButton back_button_ib = findViewById(R.id.back_button_ib);
        back_button_ib.setOnClickListener(v ->
        {
            super.onBackPressed();
        });

        /*
        nested_scroll_view_nsv.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) ->
        {

        });

         */


    }
}