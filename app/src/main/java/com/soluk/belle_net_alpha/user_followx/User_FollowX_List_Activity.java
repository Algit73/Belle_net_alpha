package com.soluk.belle_net_alpha.user_followx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.soluk.belle_net_alpha.R;

import org.jetbrains.annotations.NotNull;

public class User_FollowX_List_Activity extends AppCompatActivity
{

    String TAG = FollowX_Fragment.class.getSimpleName();
    private static String  body;
    private ViewPager2 view_pager;
    private static final int NUMBER_OF_PAGES = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_followx_list);

        Intent intent = getIntent();
        body = intent.getStringExtra("body");
        int mode = intent.getIntExtra("mode",0);

        update_followx_type(mode);

        ImageButton back_button_ib = findViewById(R.id.back_button_ib);
        back_button_ib.setOnClickListener(v ->
        {
            super.onBackPressed();
        });


        view_pager = findViewById(R.id.pager_user_followx);
        RecyclerView.Adapter pager_adapter = new main_ui_pager_adapter(this);
        view_pager.setAdapter(pager_adapter);
        view_pager.setPageTransformer(null);
        view_pager.setUserInputEnabled(false);
        view_pager.setPageTransformer(null);
        view_pager.setCurrentItem(mode,false);

        TabLayout tab_layout = findViewById(R.id.followx_tab);
        //tab_layout.selectTab(tab_layout.getTabAt(mode));
        tab_layout.getTabAt(mode).select();

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                view_pager.setCurrentItem(tab.getPosition(),false);
                update_followx_type(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

    }

    public void update_followx_type(int mode)
    {
        TextView followx_type_tv = findViewById(R.id.followx_type_tv);
        if(mode>0)
            followx_type_tv.setText("Followers");
        else
            followx_type_tv.setText("Followings");
    }

    private static class main_ui_pager_adapter extends FragmentStateAdapter
    {
        public main_ui_pager_adapter(FragmentActivity fa)
        {super(fa);}

        @Override
        public @NotNull Fragment createFragment(int position)
        {return FollowX_Fragment.newInstance(body,position);}

        @Override
        public int getItemCount()
        {return NUMBER_OF_PAGES;}
    }


}