package com.soluk.belle_net_alpha.selected_user_profile_page;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.soluk.belle_net_alpha.R;

public class User_Profile_Page_Activity extends AppCompatActivity
{

    private FragmentTransaction fragment_transaction;
    User_Profile_Personal_Info_Fragment user_profile_personal_info_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);

        Intent intent = getIntent();
        String extra = intent.getStringExtra("feature");

        ImageButton back_button_ib = findViewById(R.id.back_button_ib);
        back_button_ib.setOnClickListener(v ->
        {
            super.onBackPressed();
        });

        user_profile_personal_info_fragment = User_Profile_Personal_Info_Fragment.newInstance(extra);

        if (savedInstanceState == null)
        {
            fragment_transaction =  getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    //.add(R.id.fragment_map_section, selected_event_map_fragment, null)
                    //.add(R.id.fragment_info_section, selected_event_info_fragment,null)
                    //.add(R.id.fragment_followers_section, selected_event_followers_fragment,null)
                    .add(R.id.fragment_user_profile_personal_info, user_profile_personal_info_fragment,null);
            fragment_transaction.commit();
        }

    }
}