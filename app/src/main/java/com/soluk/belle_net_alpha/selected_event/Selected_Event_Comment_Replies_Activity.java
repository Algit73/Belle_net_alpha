package com.soluk.belle_net_alpha.selected_event;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import com.soluk.belle_net_alpha.R;

public class Selected_Event_Comment_Replies_Activity extends AppCompatActivity
{
    private FragmentTransaction fragment_transaction;
    private Selected_Event_Comment_Replies_Fragment selected_event_comment_replies_fragment;
    private final String TAG = Selected_Event_Comment_Replies_Fragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_event_comment_replies);

        Intent intent = getIntent();
        String extra = intent.getStringExtra("comment_id");
        selected_event_comment_replies_fragment = Selected_Event_Comment_Replies_Fragment.newInstance(extra);

        if (savedInstanceState == null)
        {
            fragment_transaction =  getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_comment_replies, selected_event_comment_replies_fragment, null);
            fragment_transaction.commit();
        }

        /*
        ImageButton back_button_ib = findViewById(R.id.back_button_ib);
        back_button_ib.setOnClickListener(v ->
        {
            super.onBackPressed();
        });
         */
    }
}