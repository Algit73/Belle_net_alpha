package com.soluk.belle_net_alpha.search_users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.user_followx.FollowX_Fragment;
import com.soluk.belle_net_alpha.user_followx.User_FollowX_List_Activity;

import org.jetbrains.annotations.NotNull;

public class Search_Users_Activity extends AppCompatActivity
{

    private final String TAG = Search_Users_Activity.class.getSimpleName();
    private static final String REQUEST_DB_USER_FOLLOWERS_SUB_URL =
                                "belle_net_users_info/search_user_names.php";


    private FragmentTransaction fragment_transaction;
    private Search_Users_Fragment search_users_fragment;


    private static final int NUMBER_OF_PAGES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);


        search_users_fragment = new Search_Users_Fragment();

        if (savedInstanceState == null)
        {
            fragment_transaction =  getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_searched_users, search_users_fragment, null);
            fragment_transaction.commit();
        }

        TabLayout tab_layout = findViewById(R.id.event_list_parent_tab);
        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {

                switch (tab.getPosition())
                {

                    case 0:

                        Log.d(TAG, "TabSelected: Search Friends");

                        break;
                    case 1:

                        Log.d(TAG, "TabSelected: Invite Friends");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });


        ImageButton back_button_ib = findViewById(R.id.back_button_ib);
        back_button_ib.setOnClickListener(v ->
        {
            super.onBackPressed();
        });




    }


}