package com.soluk.belle_net_alpha.selected_event;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;
import com.soluk.belle_net_alpha.utils.Image_Provider;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_COMMENT;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_FAMILY;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_NAME;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_PIC;

public class Selected_Event_Add_Comment_Activity extends AppCompatActivity
{


    CircleImageView user_profile_image_civ;
    EditText user_comment_et;
    String TAG = Selected_Event_Add_Comment_Activity.class.getSimpleName();
    TextView post_comment_tv;
    Boolean post_is_enabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_event_add_comment);

        Intent intent = getIntent();
        String event_id = intent.getStringExtra(EVENT_ID);

        post_comment_tv = findViewById(R.id.post_comment_tv);
        TextView user_name_family_tv = findViewById(R.id.user_name_family_tv);
        user_profile_image_civ = findViewById(R.id.user_profile_image_civ);
        user_comment_et = findViewById(R.id.user_comment_tv);
        user_comment_et.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if  (!user_comment_et.getText().toString().isEmpty())
                {
                    post_is_enabled = true;
                    post_comment_tv.setTextColor(getResources().getColor(R.color.palette_gray_dark,getTheme()));
                }
                else
                {
                    post_is_enabled = false;
                    post_comment_tv.setTextColor(getResources().getColor(R.color.palette_gray_semi_dark,getTheme()));
                }

            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
        /// Set user's image profile
        Bitmap selected_profile_image_bmp = null;
        try
        {
            selected_profile_image_bmp = Image_Provider.get_profile_bmp(User_Credentials.get_item(USER_PIC));
            String name_family = User_Credentials.get_item(USER_NAME) + " " +
                                    User_Credentials.get_item(USER_FAMILY);
            user_name_family_tv.setText(name_family);
        }
        catch (JSONException e){e.printStackTrace();}
        if(selected_profile_image_bmp!=null)
            user_profile_image_civ.setImageBitmap(selected_profile_image_bmp);

        post_comment_tv.setOnClickListener(v ->
        {
            if (post_is_enabled)
            {
                Intent result = new Intent();
                result.putExtra(USER_COMMENT, user_comment_et.getText().toString());
                setResult(Activity.RESULT_OK, result);
                finish();
            }
            else
                Toast.makeText(this,"Text Cannot be Empty",Toast.LENGTH_SHORT).show();
        });

        ImageButton back_button_ib = findViewById(R.id.back_button_ib);
        back_button_ib.setOnClickListener(v ->
        {
            super.onBackPressed();
        });



    }


}