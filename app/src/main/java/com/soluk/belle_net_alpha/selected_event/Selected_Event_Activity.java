package com.soluk.belle_net_alpha.selected_event;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.ImageButton;

import com.soluk.belle_net_alpha.R;



public class Selected_Event_Activity extends AppCompatActivity
{
    private FragmentTransaction fragment_transaction;
    Selected_Event_Map_Fragment selected_event_map_fragment;
    Selected_Event_Info_Fragment selected_event_info_fragment;
    Selected_Event_Followers_Fragment selected_event_followers_fragment;
    Selected_Event_Comments_Fragement selected_event_comments_fragement;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_event);
        selected_event_map_fragment = new Selected_Event_Map_Fragment();
        selected_event_info_fragment = new Selected_Event_Info_Fragment();
        selected_event_followers_fragment = new Selected_Event_Followers_Fragment();
        selected_event_comments_fragement = new Selected_Event_Comments_Fragement();

        if (savedInstanceState == null)
        {
            fragment_transaction =  getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_map_section, selected_event_map_fragment, null)
                    .add(R.id.fragment_info_section, selected_event_info_fragment,null)
                    .add(R.id.fragment_followers_section, selected_event_followers_fragment,null)
                    .add(R.id.fragment_reviews_section, selected_event_comments_fragement,null);
            fragment_transaction.commit();
        }

        ImageButton back_button_ib = findViewById(R.id.back_button_ib);
        back_button_ib.setOnClickListener(v ->
        {
            super.onBackPressed();
        });


    }
}